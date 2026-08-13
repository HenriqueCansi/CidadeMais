package obj.cidademais.Core;

import android.graphics.Color;

public class CmConstantes
{
	public static final String STATUS_ABERTA = "ABERTA";
	public static final String STATUS_EM_ANALISE = "EM_ANALISE";
	public static final String STATUS_ENCAMINHADA = "ENCAMINHADA";
	public static final String STATUS_RESOLVIDA = "RESOLVIDA";

	public static final String TIPO_USUARIO_USER = "USER";
	public static final String TIPO_USUARIO_ADMIN = "ADMIN";

	public static String rotuloStatus(String status)
	{
		if (status == null)
			return "Aberta";

		switch (status)
		{
			case STATUS_EM_ANALISE: return "Em análise";
			case STATUS_ENCAMINHADA: return "Encaminhada";
			case STATUS_RESOLVIDA: return "Resolvida";
			case STATUS_ABERTA:
			default:
				return "Aberta";
		}
	}

	public static int corStatus(String status)
	{
		if (status == null)
			return Color.parseColor("#E53935");

		switch (status)
		{
			case STATUS_EM_ANALISE: return Color.parseColor("#FB8C00");
			case STATUS_ENCAMINHADA: return Color.parseColor("#1E88E5");
			case STATUS_RESOLVIDA: return Color.parseColor("#43A047");
			case STATUS_ABERTA:
			default:
				return Color.parseColor("#E53935");
		}
	}
}
