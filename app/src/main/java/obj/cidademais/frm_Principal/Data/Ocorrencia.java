package obj.cidademais.frm_Principal.Data;

import com.google.firebase.Timestamp;

import java.util.List;

public class Ocorrencia
{
	public String id;

	public String uidUsuario;

	public String titulo;

	public String descricao;

	public String categoria;

	public String status;

	public double latitude;

	public double longitude;

	public String endereco;

	public String cidade;

	public List<String> fotos;

	public int curtidas;

	public int confirmacoes;

	public int comentarios;

	public Timestamp criadoEm;

	public Timestamp atualizadoEm;
}