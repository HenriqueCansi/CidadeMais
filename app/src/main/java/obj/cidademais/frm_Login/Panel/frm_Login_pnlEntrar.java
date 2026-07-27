package obj.cidademais.frm_Login.Panel;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlPrincipal;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;

public class frm_Login_pnlEntrar extends RvView
{
	public static frm_Login_pnlEntrar __obj = new frm_Login_pnlEntrar();
	private LinearLayout layout;
	private FirebaseAuth mAuth;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_login_pnlentrar, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		EditText edtEmail = layout.findViewById(R.id.edtEmail);
		EditText edtSenha = layout.findViewById(R.id.edtSenha);
		Button btnEntrar = layout.findViewById(R.id.btnEntrar);
		TextView txtNaoTenhoCadastro = layout.findViewById(R.id.tvCadastrarAgora);
		ImageView ivVoltar = layout.findViewById(R.id.ivVoltar);

		mAuth = FirebaseAuth.getInstance();

		btnEntrar.setOnClickListener(v -> {
			String email = edtEmail.getText().toString();
			String senha = edtSenha.getText().toString();

			if (email.isEmpty() || senha.isEmpty())
			{
				Toast.makeText(RvActivity.__activity, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
				return;
			}

			mAuth.signInWithEmailAndPassword(email, senha)
					.addOnCompleteListener(task -> {

						if (task.isSuccessful())
						{
							String uid = mAuth.getCurrentUser().getUid();

							FirebaseUsuario.buscar(uid, new FirebaseUsuario.CallbackBusca()
							{
								@Override
								public void onSucesso(Usuario usuario)
								{
									Sessao.setUsuario(usuario);

									Toast.makeText(RvActivity.__activity,
											"Bem-vindo " + usuario.nome,
											Toast.LENGTH_SHORT).show();

									if (frm_Principal_pnlPrincipal.__obj == null)
										frm_Principal_pnlPrincipal.__obj = new frm_Principal_pnlPrincipal();

									frm_Principal_pnlPrincipal.__obj.Show();
									Hide();
								}

								@Override
								public void onErro(Exception e)
								{
									Toast.makeText(RvActivity.__activity,
											"Erro ao carregar usuário: " + e.getMessage(),
											Toast.LENGTH_LONG).show();
								}
							});
						}
					});

		});

		txtNaoTenhoCadastro.setOnClickListener(v -> {
			frm_Login_pnlCadastrar.__obj.Show();
			this.Hide();
		});

		ivVoltar.setOnClickListener(v -> {
			if(frm_Login_pnlLogin.__obj == null)
				frm_Login_pnlLogin.__obj = new frm_Login_pnlLogin();

			frm_Login_pnlLogin.__obj.Show();
			this.Hide();
		});

	}
}
