package obj.cidademais.frm_Login.Data;

import java.sql.Timestamp;

public class Usuario
{
	public String uid;
	public String nome;
	public String email;
	public String tipoUsuario;
	public int reputacao;
	public int nivel;
	public String cidade;
	public String fotoPerfil;
	public String fcmToken;
	public boolean ativo;
	public com.google.firebase.Timestamp criadoEm;
	public com.google.firebase.Timestamp ultimoLogin;

	public Usuario()
	{

	}
}
