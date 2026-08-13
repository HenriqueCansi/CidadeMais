package obj.cidademais.Core;

public class CmReputacao
{
	public static final int PESO_CURTIDA = 1;
	public static final int PESO_CONFIRMACAO = 2;

	public static final int PESO_OCORRENCIA_REGISTRADA = 5;
	public static final int PESO_OCORRENCIA_RESOLVIDA = 10;

	public static final int PONTOS_POR_NIVEL = 100;

	public static int pontuacaoEngajamento(int curtidas, int confirmacoes)
	{
		return curtidas * PESO_CURTIDA + confirmacoes * PESO_CONFIRMACAO;
	}

	public static int calcularReputacao(int qtdOcorrencias, int totalCurtidas, int totalConfirmacoes, int qtdResolvidas)
	{
		return qtdOcorrencias * PESO_OCORRENCIA_REGISTRADA
				+ pontuacaoEngajamento(totalCurtidas, totalConfirmacoes)
				+ qtdResolvidas * PESO_OCORRENCIA_RESOLVIDA;
	}

	public static int calcularNivel(int reputacao)
	{
		return 1 + (reputacao / PONTOS_POR_NIVEL);
	}
}
