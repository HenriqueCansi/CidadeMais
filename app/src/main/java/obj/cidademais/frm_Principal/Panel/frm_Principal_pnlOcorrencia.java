package obj.cidademais.frm_Principal.Panel;

import com.google.firebase.auth.FirebaseAuth;

import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Login.Panel.frm_Login_pnlCadastrar;

public class frm_Principal_pnlOcorrencia extends RvView
{
	public static frm_Principal_pnlOcorrencia __obj = new frm_Principal_pnlOcorrencia();
	private LinearLayout layout;
	private FirebaseAuth mAuth;
	EditText edtTitulo;
	EditText edtDescricao;

	Spinner spCategoria;

	ImageView imgPreview;

	TextView txtEndereco;
	TextView txtCidade;

	Button btnAdicionarImagem;

	Button btnCancelar;
	Button btnReportar;

	Button btnAlterarLocalizacao;
	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlocorrencia, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		 edtTitulo = layout.findViewById(R.id.edtTitulo);
		 edtDescricao = layout.findViewById(R.id.edtDescricao);

		 spCategoria = layout.findViewById(R.id.spCategoria);

		 imgPreview = layout.findViewById(R.id.imgPreview);

		 txtEndereco = layout.findViewById(R.id.txtEndereco);
		 txtCidade = layout.findViewById(R.id.txtCidade);

		 btnAdicionarImagem = layout.findViewById(R.id.btnAdicionarImagem);

		 btnCancelar = layout.findViewById(R.id.btnCancelar);
		 btnReportar = layout.findViewById(R.id.btnReportar);

		 btnAlterarLocalizacao = layout.findViewById(R.id.btnAlterarLocalizacao);

		carregarCategorias();

		carregarLocalizacao();

		btnAdicionarImagem.setOnClickListener(v -> {

			Toast.makeText(RvActivity.__activity,
					"Selecionar imagem",
					Toast.LENGTH_SHORT).show();

			// Abrir câmera/galeria
		});

		btnAlterarLocalizacao.setOnClickListener(v -> {

			Toast.makeText(RvActivity.__activity,
					"Abrir mapa",
					Toast.LENGTH_SHORT).show();

			// Abrir mapa para selecionar outro local
		});

		btnCancelar.setOnClickListener(v -> {

			frm_Principal_pnlPrincipal.__obj.Show();
			Hide();

		});

		btnReportar.setOnClickListener(v -> {

			String titulo = edtTitulo.getText().toString().trim();
			String descricao = edtDescricao.getText().toString().trim();
			String categoria = spCategoria.getSelectedItem().toString();

			if (titulo.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Informe o título.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			if (descricao.isEmpty())
			{
				Toast.makeText(RvActivity.__activity,
						"Informe a descrição.",
						Toast.LENGTH_SHORT).show();
				return;
			}

			cadastrarOcorrencia(
					titulo,
					descricao,
					categoria);

		});
	}

	private void carregarCategorias()
	{

	}

	private void carregarLocalizacao()
	{

	}

	private void cadastrarOcorrencia(String titulo, String descricao, String categoria)
	{

	}

	public void ShowCustom()
	{

	}


}
