package obj.cidademais.Firebase.Ocorrencia;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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

	public static void cadastrarComFotos(
			Ocorrencia ocorrencia,
			List<Uri> fotos,
			CallbackCadastro callback)
	{
		if (fotos == null || fotos.isEmpty())
		{
			cadastrar(ocorrencia, callback);
			return;
		}

		List<Task<Uri>> uploads = new ArrayList<>();

		for (Uri foto : fotos)
		{
			StorageReference ref = FirebaseStorage
					.getInstance()
					.getReference("ocorrencias/" + UUID.randomUUID() + ".jpg");

			Task<Uri> upload = ref.putFile(foto)
					.continueWithTask(task -> ref.getDownloadUrl());

			uploads.add(upload);
		}

		Tasks.whenAllSuccess(uploads)
				.addOnSuccessListener(resultados -> {

					List<String> urls = new ArrayList<>();

					for (Object resultado : resultados)
						urls.add(resultado.toString());

					ocorrencia.fotos = urls;

					cadastrar(ocorrencia, callback);

				})
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

	public interface CallbackBusca
	{
		void onSucesso(Ocorrencia ocorrencia);

		void onErro(Exception e);
	}

	public static void buscarPorId(String id, CallbackBusca callback)
	{
		FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.document(id)
				.get()
				.addOnSuccessListener(documentSnapshot -> {

					if (documentSnapshot.exists())
						callback.onSucesso(documentSnapshot.toObject(Ocorrencia.class));
					else
						callback.onErro(new Exception("Ocorrência não encontrada."));

				})
				.addOnFailureListener(callback::onErro);
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

	public static ListenerRegistration observarTodas(CallbackListagem callback)
	{
		return FirebaseFirestore
				.getInstance()
				.collection("ocorrencias")
				.addSnapshotListener((querySnapshot, erro) -> {

					if (erro != null)
					{
						callback.onErro(erro);
						return;
					}

					List<Ocorrencia> lista = new ArrayList<>();

					for (var documento : querySnapshot.getDocuments())
					{
						Ocorrencia ocorrencia = documento.toObject(Ocorrencia.class);

						if (ocorrencia != null)
							lista.add(ocorrencia);
					}

					callback.onSucesso(lista);

				});
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
