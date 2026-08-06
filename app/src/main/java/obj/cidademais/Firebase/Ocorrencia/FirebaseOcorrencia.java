package obj.cidademais.Firebase.Ocorrencia;

import com.google.firebase.firestore.FirebaseFirestore;

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
}
