package obj.cidademais.Firebase.Ocorrencia;

import android.net.Uri;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import obj.cidademais.frm_Login.Classe.CallbackCadastro;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class FirebaseOcorrencia
{
	public static void cadastrar(
			Ocorrencia ocorrencia,
			CallbackCadastro callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.add(ocorrencia)
				.addOnSuccessListener(documentReference -> {

					ocorrencia.id = documentReference.getId();

					documentReference.update("id", ocorrencia.id)
							.addOnSuccessListener(unused ->
									callback.onSucesso())
							.addOnFailureListener(callback::onErro);

				})
				.addOnFailureListener(callback::onErro);
	}

	public static void cadastrarComFoto(
			Ocorrencia ocorrencia,
			Uri fotoUri,
			CallbackCadastro callback)
	{
		FirebaseStorage
				.getInstance()
				.getReference("ocorrencias/" + UUID.randomUUID() + ".jpg")
				.putFile(fotoUri)
				.addOnSuccessListener(taskSnapshot ->
						taskSnapshot.getStorage().getDownloadUrl()
								.addOnSuccessListener(url -> {

									ocorrencia.fotos = Collections.singletonList(url.toString());

									cadastrar(ocorrencia, callback);

								})
								.addOnFailureListener(callback::onErro))
				.addOnFailureListener(callback::onErro);
	}

	public static void alternarCurtida(
			String ocorrenciaId,
			String uid,
			boolean curtir,
			CallbackCadastro callback)
	{
		Map<String, Object> updates = new HashMap<>();

		updates.put("curtidas", FieldValue.increment(curtir ? 1 : -1));
		updates.put("curtidoPor", curtir ? FieldValue.arrayUnion(uid) : FieldValue.arrayRemove(uid));

		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.document(ocorrenciaId)
				.update(updates)
				.addOnSuccessListener(unused -> callback.onSucesso())
				.addOnFailureListener(callback::onErro);
	}

	public static void alternarConfirmacao(
			String ocorrenciaId,
			String uid,
			boolean confirmar,
			CallbackCadastro callback)
	{
		Map<String, Object> updates = new HashMap<>();

		updates.put("confirmacoes", FieldValue.increment(confirmar ? 1 : -1));
		updates.put("confirmadoPor", confirmar ? FieldValue.arrayUnion(uid) : FieldValue.arrayRemove(uid));

		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.document(ocorrenciaId)
				.update(updates)
				.addOnSuccessListener(unused -> callback.onSucesso())
				.addOnFailureListener(callback::onErro);
	}

	public static void atualizarStatus(
			String ocorrenciaId,
			String novoStatus,
			CallbackCadastro callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.document(ocorrenciaId)
				.update("status", novoStatus)
				.addOnSuccessListener(unused -> callback.onSucesso())
				.addOnFailureListener(callback::onErro);
	}

	public interface CallbackListagem
	{
		void onSucesso(List<Ocorrencia> lista);

		void onErro(Exception e);
	}

	public static void listarTodas(CallbackListagem callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.get()
				.addOnSuccessListener(querySnapshot -> {

					List<Ocorrencia> lista = new ArrayList<>();

					for (var documento : querySnapshot.getDocuments())
					{
						Ocorrencia ocorrencia = documento.toObject(Ocorrencia.class);

						if (ocorrencia != null)
							lista.add(ocorrencia);
					}

					callback.onSucesso(lista);

				})
				.addOnFailureListener(callback::onErro);
	}

	public static void listarPorUsuario(String uid, CallbackListagem callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.whereEqualTo("uidUsuario", uid)
				.get()
				.addOnSuccessListener(querySnapshot -> {

					List<Ocorrencia> lista = new ArrayList<>();

					for (var documento : querySnapshot.getDocuments())
					{
						Ocorrencia ocorrencia = documento.toObject(Ocorrencia.class);

						if (ocorrencia != null)
							lista.add(ocorrencia);
					}

					callback.onSucesso(lista);

				})
				.addOnFailureListener(callback::onErro);
	}
}
