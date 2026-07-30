package obj.cidademais.Core.Localizacao;

import obj.cidademais.Core.Localizacao.CmPosicao;

public class CmGPS
{
	public interface Callback
	{
		void onSucesso(CmPosicao posicao);

		void onErro(String erro);
	}

	public static void buscar(Callback callback)
	{

	}
}