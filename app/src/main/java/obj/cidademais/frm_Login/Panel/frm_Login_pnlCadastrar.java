package obj.cidademais.frm_Login.Panel;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;

public class frm_Login_pnlCadastrar extends RvView
{
	public static frm_Login_pnlCadastrar __obj = new frm_Login_pnlCadastrar();
	private LinearLayout layout;
	private FirebaseAuth mAuth;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_login_pnlcadastrar, null);

		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		EditText edtNome = layout.findViewById(R.id.edtNome);
		EditText edtEmail = layout.findViewById(R.id.edtEmail);
		EditText edtSenha = layout.findViewById(R.id.edtSenha);
		CheckBox chkTermos = layout.findViewById(R.id.chkTermos);
		Button btnCadastrar = layout.findViewById(R.id.btnCadastrar);
		TextView txtJaTenhoCadastro = layout.findViewById(R.id.txtJaTenhoCadastro);
		ImageButton btnGoogleLogin = layout.findViewById(R.id.btnGoogleLogin);
		mAuth = FirebaseAuth.getInstance();

		btnCadastrar.setOnClickListener(v -> {
			if (!chkTermos.isChecked())
			{
				Toast.makeText(RvActivity.__activity, "Você precisa aceitar os termos", Toast.LENGTH_SHORT).show();
				return;
			}

			String nome = edtNome.getText().toString();
			String email = edtEmail.getText().toString();
			String senha = edtSenha.getText().toString();

			if (nome.isEmpty() || email.isEmpty() || senha.isEmpty())
			{
				Toast.makeText(RvActivity.__activity, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
				return;
			}

			mAuth.createUserWithEmailAndPassword(email, senha)
					.addOnCompleteListener(task -> {
						if (task.isSuccessful())
						{
							criaUsuario(nome, email);
							return;
						}
						else
						{
							Toast.makeText(RvActivity.__activity, "Erro ao cadastrar: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
						}
					});
		});

		txtJaTenhoCadastro.setOnClickListener(v -> {
			frm_Login_pnlEntrar.__obj = new frm_Login_pnlEntrar();
			frm_Login_pnlEntrar.__obj.Show();
			this.Hide();
		});

		btnGoogleLogin.setOnClickListener(v -> {
			Toast.makeText(RvActivity.__activity, "Login com Google ainda não implementado", Toast.LENGTH_SHORT).show();
		});

	}

	public void criaUsuario(String paNome, String paEmail)
	{

		String uid = mAuth.getCurrentUser().getUid();

		Usuario usuario = new Usuario();

		usuario.uid = uid;
		usuario.nome = paNome;
		usuario.email = paEmail;

		usuario.tipoUsuario = "USER";

		usuario.reputacao = 0;
		usuario.nivel = 1;

		usuario.cidade = "";
		usuario.fotoPerfil = "";

		usuario.ativo = true;

		usuario.criadoEm = Timestamp.now();
		usuario.ultimoLogin = Timestamp.now();

		FirebaseUsuario.salvar(usuario, new FirebaseUsuario.Callback()
		{
			@Override
			public void onSucesso()
			{
				Toast.makeText(RvActivity.__activity,
						"Cadastro realizado com sucesso!",
						Toast.LENGTH_SHORT).show();

				frm_Principal_pnlPrincipal __obj = new frm_Principal_pnlPrincipal();
				frm_Principal_pnlPrincipal.__obj.Show();
				Hide();
			}

			@Override
			public void onErro(Exception e)
			{
				Toast.makeText(RvActivity.__activity,
						"Erro ao salvar usuário: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				return;
			}
		});

	}
}
