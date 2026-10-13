package obj.cidademais.Firebase.Mensagem;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.frm_Login.Data.Sessao;

public class CmFirebaseMessagingService extends FirebaseMessagingService
{
	private static final String CANAL_ID = "ocorrencias_status";

	@Override
	public void onNewToken(String token)
	{
		super.onNewToken(token);

		if (Sessao.isLogado())
			FirebaseUsuario.atualizarToken(Sessao.getUsuario().uid, token, null);
	}

	@Override
	public void onMessageReceived(RemoteMessage message)
	{
		super.onMessageReceived(message);

		String titulo;
		String corpo;

		if (message.getNotification() != null)
		{
			titulo = message.getNotification().getTitle();
			corpo = message.getNotification().getBody();
		}
		else
		{
			titulo = message.getData().get("titulo");
			corpo = message.getData().get("corpo");
		}

		if (titulo == null && corpo == null)
			return;

		NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, CANAL_ID)
				.setSmallIcon(R.drawable.ic_sino)
				.setContentTitle(titulo)
				.setContentText(corpo)
				.setAutoCancel(true);

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
		{
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		NotificationManagerCompat.from(this).notify(
				(int) System.currentTimeMillis(),
				notificacao.build());
	}
}
