package obj.cidademais.Core.Localizacao;

public class Localizacao
{
	public String endereco;

	public String numero;

	public String bairro;

	public String cidade;

	public String estado;

	public String cep;

	public String pais;

	@Override
	public String toString()
	{
		return endereco + ", " + numero;
	}
}