package obj.cidademais.Core.Localizacao;

public class CmPosicao
{
	public static CmPosicao posicaoAtual;
	public double latitude;

	public double longitude;

	public float precisao;

	public long horario;

	public boolean gpsAtivo;

	public Localizacao localizacao = new Localizacao();
}