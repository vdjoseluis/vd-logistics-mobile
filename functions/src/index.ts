import * as admin from "firebase-admin";
import {onDocumentCreated, onDocumentDeleted, onDocumentUpdated}
  from "firebase-functions/v2/firestore";
import {logger} from "firebase-functions";

admin.initializeApp();

/**
* Envía una notificación push a un dispositivo.
* @param {string} fcmToken - Token FCM del destinatario.
* @param {string} title - Título de la notificación.
* @param {string} body - Cuerpo de la notificación.
* @param {object} data - Datos adicionales para la notificación.
* @return {Promise<void>} - Promise de JS-TS
*/
async function sendNotification(fcmToken: string,
  title: string, body: string, data: Record<string, string>) {
  if (!fcmToken) {
    logger.warn("El operador no tiene un token FCM registrado.");
    return;
  }

  const message: admin.messaging.Message = {
    token: fcmToken,
    notification: {title, body},
    data,
  };

  try {
    await admin.messaging().send(message);
    logger.info(`Notificación enviada: ${title}`);
  } catch (error) {
    logger.error(`Error al enviar notificación: ${error}`);
  }
}

/**
* Obtiene el token FCM del operador asignado al servicio.
* @param {FirebaseFirestore.DocumentReference<FirebaseFirestore.DocumentData>}
* refOperator - Referencia al documento del operador.
* @return {Promise<string | null>} Token del operador o `null` si no existe.
*/
async function getOperatorToken(refOperator: any): Promise<string | null> {
  if (!refOperator) return null;

  const operatorSnapshot = await refOperator.get();
  if (!operatorSnapshot.exists) return null;

  return operatorSnapshot.data()?.fcmToken || null;
}

/**
* Notifica cuando se crea un nuevo servicio.
*/
export const onServiceCreated = onDocumentCreated("services/{serviceId}",
  async (event) => {
    const newData = event.data?.data();
    if (!newData) return;

    const serviceId = event.params.serviceId;
    const typeService = newData.type;
    const fcmToken = await getOperatorToken(newData.refOperator);

    if (fcmToken) {
      await sendNotification(
        fcmToken,
        "VD Logistics",
        `Asignado un nuevo servicio de ${typeService}.`,
        {serviceId}
      );
    }
  });

/**
* Notifica cuando se actualiza un servicio existente.
*/
export const onServiceUpdated = onDocumentUpdated("services/{serviceId}",
  async (event) => {
    const change = event.data;
    if (!change) return;

    const serviceId = event.params.serviceId;
    const afterData = change.after.data();
    const typeService = afterData.type;
    const fcmToken = await getOperatorToken(afterData.refOperator);

    if (fcmToken) {
      const preNofification: string =
      (afterData.status==="Cancelado") ? "Cancelado" : "Actualizado";
      await sendNotification(
        fcmToken,
        "VD Logistics",
        `${preNofification} un servicio de ${typeService}.`,
        {serviceId}
      );
    }
  });

/**
* Notifica cuando se elimina un servicio.
*/
export const onServiceDeleted = onDocumentDeleted("services/{serviceId}",
  async (event) => {
    const deletedData = event.data?.data();
    if (!deletedData) return;

    const serviceId = event.params.serviceId;
    const typeService = deletedData.type;
    const fcmToken = await getOperatorToken(deletedData.refOperator);

    if (fcmToken) {
      await sendNotification(
        fcmToken,
        "VD Logistics",
        `Se ha eliminado un servicio de ${typeService}.`,
        {serviceId}
      );
    }
  });
