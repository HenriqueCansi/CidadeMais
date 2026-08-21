package obj.cidademais.frm_Login.Panel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import obj.cidademais.frm_Login.Panel.frm_Login_pnlLogin;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import obj.cidademais.Core.CmCamera;
import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.CmGaleria;
import obj.cidademais.Core.CmPermissao;
import obj.cidademais.Core.CmUi;
import obj.cidademais.Firebase.Usuario.FirebaseUsuario;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Data.Sessao;
import obj.cidademais.frm_Login.Data.Usuario;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlPrincipal;
import obj.cidademais.frm_Principal.Panel.frm_Principal_pnlPrincipal;

public class frm_Login_pnlCadastrar extends RvView
{
	public static frm_Login_pnlCadastrar __obj = new frm_Login_pnlCadastrar();
	private LinearLayout layout;
	private FirebaseAuth mAuth;
	private ImageView imgFotoPerfil;
	private Uri fotoPerfilSelecionada;

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
		ImageView ivVoltar = layout.findViewById(R.id.ivVoltar);
		imgFotoPerfil = layout.findViewById(R.id.imgFotoPerfil);
		TextView tvAdicionarFoto = layout.findViewById(R.id.tvAdicionarFoto);
		CmUi.arredondar(imgFotoPerfil, 44);

		mAuth = FirebaseAuth.getInstance();

		imgFotoPerfil.setOnClickListener(v -> escolherFotoPerfil());
		tvAdicionarFoto.setOnClickListener(v -> escolherFotoPerfil());

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

			if (fotoPerfilSelecionada == null)
			{
				Toast.makeText(RvActivity.__activity, "Adicione uma foto de perfil.", Toast.LENGTH_SHORT).show();
				return;
			}

			RvActivity.mostrarCarregando("Cadastrando...");

			mAuth.createUserWithEmailAndPassword(email, senha)
					.addOnCompleteListener(task -> {
						if (task.isSuccessful())
						{
							criaUsuario(nome, email);
							return;
						}
						else
						{
							RvActivity.esconderCarregando();
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

		ivVoltar.setOnClickListener(v -> {
			if(frm_Login_pnlLogin.__obj == null)
				frm_Login_pnlLogin.__obj = new frm_Login_pnlLogin();

			frm_Login_pnlLogin.__obj.Show();
			this.Hide();
		});
	}

	private void escolherFotoPerfil()
	{
		new AlertDialog.Builder(RvActivity.__activity)
				.setTitle("Foto de perfil")
				.setItems(new CharSequence[]{"Tirar foto", "Escolher da galeria"}, (dialog, which) -> {
					if (which == 0)
						tirarFotoPerfil();
					else
						CmGaleria.abrirGaleria(new CmGaleria.Callback()
						{
							@Override
							public void onSucesso(Uri fotoUri)
							{
								fotoPerfilSelecionada = fotoUri;
								imgFotoPerfil.setImageURI(fotoUri);
							}

							@Override
							public void onErro(String erro)
							{
								Toast.makeText(RvActivity.__activity, erro, Toast.LENGTH_SHORT).show();
							}
						});
				})
				.show();
	}

	private void tirarFotoPerfil()
	{
		if (ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
		{
			CmPermissao.solicitarPermissoes(new CmPermissao.Callback()
			{
				@Override
				public void onPermitido()
				{
					tirarFotoPerfil();
				}

				@Override
				public void onNegado()
				{
					Toast.makeText(RvActivity.__activity, "Permissão de câmera negada.", Toast.LENGTH_SHORT).show();
				}
			});
			return;
		}

		CmCamera.tirarFoto(new CmCamera.Callback()
		{
			@Override
			public void onSucesso(Uri fotoUri)
			{
				fotoPerfilSelecionada = fotoUri;
				imgFotoPerfil.setImageURI(fotoUri);
			}

			@Override
			public void onErro(String erro)
			{
				Toast.makeText(RvActivity.__activity, erro, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void criaUsuario(String paNome, String paEmail)
	{

		String uid = mAuth.getCurrentUser().getUid();

		Usuario usuario = new Usuario();

		usuario.uid = uid;
		usuario.nome = paNome;
		usuario.email = paEmail;

		usuario.tipoUsuario = CmConstantes.TIPO_USUARIO_USER;

		usuario.reputacao = 0;
		usuario.nivel = 1;

		usuario.cidade = "";

		usuario.ativo = true;

		usuario.criadoEm = Timestamp.now();
		usuario.ultimoLogin = Timestamp.now();

		FirebaseUsuario.cadastrarComFoto(usuario, fotoPerfilSelecionada, new FirebaseUsuario.Callback()
		{
			@Override
			public void onSucesso()
			{
				RvActivity.esconderCarregando();

				Sessao.setUsuario(usuario);

				FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token ->
						FirebaseUsuario.atualizarToken(usuario.uid, token, null));

				Toast.makeText(RvActivity.__activity,
						"Cadastro realizado com sucesso!",
						Toast.LENGTH_SHORT).show();

				if (frm_Principal_pnlPrincipal.__obj == null)
					frm_Principal_pnlPrincipal.__obj = new frm_Principal_pnlPrincipal();

				frm_Principal_pnlPrincipal.__obj.Show();
				Hide();
			}

			@Override
			public void onErro(Exception e)
			{
				RvActivity.esconderCarregando();

				Toast.makeText(RvActivity.__activity,
						"Erro ao salvar usuário: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				return;
			}
		});


	}
}
