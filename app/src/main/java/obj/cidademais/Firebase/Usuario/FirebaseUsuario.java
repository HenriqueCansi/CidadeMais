package obj.cidademais.Firebase.Usuario;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import obj.cidademais.frm_Login.Data.Usuario;

public class FirebaseUsuario
{
	private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

	public interface Callback
	{
		void onSucesso();

		void onErro(@NonNull Exception e);
	}

	public static void salvar(Usuario usuario, Callback callback)
	{
		db.collection("usuarios")
				.document(usuario.uid)
				.set(usuario)
				.addOnSuccessListener(unused -> {

					if (callback != null)
						callback.onSucesso();

				})
				.addOnFailureListener(e -> {

					if (callback != null)
						callback.onErro(e);

				});
	}
}