package obj.cidademais.Core;

import com.google.firebase.Timestamp;

public class CmData
{
	public static String tempoRelativo(Timestamp criadoEm)
	{
		if (criadoEm == null)
			return "";

		long diffMs = System.currentTimeMillis() - criadoEm.toDate().getTime();
		long minutos = diffMs / 60000;

		if (minutos < 1)
			return "agora";

		if (minutos < 60)
			return minutos + " minutos atrás";

		long horas = minutos / 60;

		if (horas < 24)
			return horas + " horas atrás";

		long dias = horas / 24;

		return dias + " dias atrás";
	}
}
