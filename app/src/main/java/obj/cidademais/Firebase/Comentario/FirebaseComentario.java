package obj.cidademais.Firebase.Comentario;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

import obj.cidademais.frm_Login.Classe.CallbackCadastro;
import obj.cidademais.frm_Principal.Data.Comentario;

public class FirebaseComentario
{
	public interface CallbackListagem
	{
		void onSucesso(List<Comentario> lista);

		void onErro(Exception e);
	}

	public static void adicionar(Comentario comentario, CallbackCadastro callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("comentarios")
				.add(comentario)
				.addOnSuccessListener(documentReference ->

						FirebaseFirestore
								.getInstance()
								.collection("ocorrencias")
								.document(comentario.ocorrenciaId)
								.update("comentarios", FieldValue.increment(1))
								.addOnSuccessListener(unused -> callback.onSucesso())
								.addOnFailureListener(callback::onErro)

				)
				.addOnFailureListener(callback::onErro);
	}

	public static ListenerRegistration observarPorOcorrencia(String ocorrenciaId, CallbackListagem callback)
	{
		return FirebaseFirestore
				.getInstance()
				.collection("comentarios")
				.whereEqualTo("ocorrenciaId", ocorrenciaId)
				.addSnapshotListener((querySnapshot, erro) -> {

					if (erro != null)
					{
						callback.onErro(erro);
						return;
					}

					List<Comentario> lista = new ArrayList<>();

					for (var documento : querySnapshot.getDocuments())
					{
						Comentario comentario = documento.toObject(Comentario.class);

						if (comentario != null)
						{
							comentario.id = documento.getId();
							lista.add(comentario);
						}
					}

					callback.onSucesso(lista);

				});
	}
}
