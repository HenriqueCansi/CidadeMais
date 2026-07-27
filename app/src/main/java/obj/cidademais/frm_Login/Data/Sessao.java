package obj.cidademais.frm_Login.Data;

import obj.cidademais.frm_Login.Data.Usuario;

public class Sessao
{
	private static Usuario usuario;

	public static void setUsuario(Usuario paUsuario)
	{
		usuario = paUsuario;
	}

	public static Usuario getUsuario()
	{
		return usuario;
	}

	public static boolean isLogado()
	{
		return usuario != null;
	}

	public static void limpar()
	{
		usuario = null;
	}
}