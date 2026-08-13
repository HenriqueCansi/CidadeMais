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

	public static void atualizarToken(String uid, String token, Callback callback)
	{
		db.collection("usuarios")
				.document(uid)
				.update("fcmToken", token)
				.addOnSuccessListener(unused -> {

					if (callback != null)
						callback.onSucesso();

				})
				.addOnFailureListener(e -> {

					if (callback != null)
						callback.onErro(e);

				});
	}

	public interface CallbackBusca
	{
		void onSucesso(Usuario usuario);

		void onErro(Exception e);
	}

	public static void buscar(String uid, CallbackBusca callback)
	{
		db.collection("usuarios")
				.document(uid)
				.get()
				.addOnSuccessListener(documentSnapshot -> {

					if (documentSnapshot.exists())
					{
						Usuario usuario = documentSnapshot.toObject(Usuario.class);

						if (callback != null)
							callback.onSucesso(usuario);
					}
					else
					{
						if (callback != null)
							callback.onErro(new Exception("Usuário não encontrado."));
					}

				})
				.addOnFailureListener(e -> {

					if (callback != null)
						callback.onErro(e);

				});
	}

}