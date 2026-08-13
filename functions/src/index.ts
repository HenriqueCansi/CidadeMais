import { onDocumentUpdated } from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();
const messaging = admin.messaging();

const ROTULOS_STATUS: Record<string, string> = {
  ABERTA: "Aberta",
  EM_ANALISE: "Em análise",
  ENCAMINHADA: "Encaminhada",
  RESOLVIDA: "Resolvida",
};

function rotuloStatus(status: string | undefined): string {
  if (!status) return "Aberta";
  return ROTULOS_STATUS[status] ?? "Aberta";
}

/**
 * Dispara uma notificação push para o autor de uma ocorrência quando o
 * campo `status` do documento muda (ex: admin move de ABERTA para RESOLVIDA).
 * Não dispara em outras mudanças no mesmo documento (curtidas, confirmações, etc).
 */
export const notificarMudancaStatus = onDocumentUpdated(
  "ocorrencias/{ocorrenciaId}",
  async (event) => {
    const antes = event.data?.before.data();
    const depois = event.data?.after.data();

    if (!antes || !depois) return;

    if (antes.status === depois.status) return;

    const uidUsuario = depois.uidUsuario as string | undefined;
    if (!uidUsuario) return;

    const usuarioSnap = await db.collection("usuarios").doc(uidUsuario).get();
    if (!usuarioSnap.exists) return;

    const fcmToken = usuarioSnap.data()?.fcmToken as string | undefined;
    if (!fcmToken) {
      logger.info(`Usuário ${uidUsuario} sem fcmToken — notificação não enviada.`);
      return;
    }

    const titulo = (depois.titulo as string) ?? "Sua ocorrência";
    const corpo = `"${titulo}" agora está: ${rotuloStatus(depois.status as string)}`;

    try {
      await messaging.send({
        token: fcmToken,
        notification: {
          title: "Sua ocorrência foi atualizada",
          body: corpo,
        },
        data: {
          ocorrenciaId: event.params.ocorrenciaId,
        },
        android: {
          notification: {
            channelId: "ocorrencias_status",
          },
        },
      });
    } catch (erro) {
      logger.error(`Falha ao enviar notificação para ${uidUsuario}:`, erro);
    }
  }
);
