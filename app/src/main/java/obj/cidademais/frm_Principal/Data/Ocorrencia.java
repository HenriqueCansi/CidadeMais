package obj.cidademais.frm_Principal.Data;
import com.google.firebase.Timestamp;

import java.util.List;

import obj.cidademais.Core.Localizacao.CmPosicao;

public class Ocorrencia
{
	public String id;

	public String uidUsuario;

	public String titulo;

	public String descricao;

	public String categoria;

	public String status;

	// Localização completa
	public CmPosicao posicao;

	// Pode haver várias fotos
	public List<String> fotos;

	public int curtidas;

	public int confirmacoes;

	public int comentarios;

	public Timestamp criadoEm;

	public Timestamp atualizadoEm;
}