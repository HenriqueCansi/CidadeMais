package obj.cidademais.Firebase.Notificacao;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import obj.cidademais.frm_Notificacoes.Data.Notificacao;

public class FirebaseNotificacao
{
	public interface CallbackListagem
	{
		void onSucesso(List<Notificacao> lista);

		void onErro(Exception e);
	}

	public static void listarPorUsuario(String uid, CallbackListagem callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("notificacoes")
				.whereEqualTo("uidUsuario", uid)
				.get()
				.addOnSuccessListener(querySnapshot -> {

					List<Notificacao> lista = new ArrayList<>();

					for (var documento : querySnapshot.getDocuments())
					{
						Notificacao notificacao = documento.toObject(Notificacao.class);

						if (notificacao != null)
						{
							notificacao.id = documento.getId();
							lista.add(notificacao);
						}
					}

					callback.onSucesso(lista);

				})
				.addOnFailureListener(callback::onErro);
	}
}
