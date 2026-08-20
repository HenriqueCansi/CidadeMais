package obj.cidademais.frm_Principal.Panel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

import obj.cidademais.Core.CmConstantes;
import obj.cidademais.Core.Localizacao.CmPosicao;
import obj.cidademais.Core.Localizacao.PermissaoManager;
import obj.cidademais.Firebase.Ocorrencia.FirebaseOcorrencia;
import obj.cidademais.R;
import obj.cidademais.RvActivity;
import obj.cidademais.RvView;
import obj.cidademais.frm_Perfil_pnlPrincipal.Panel.frm_Perfil_pnlPrincipal;
import obj.cidademais.frm_Principal.Data.Ocorrencia;

public class frm_Principal_pnlPrincipal extends RvView implements OnMapReadyCallback
{
	public static frm_Principal_pnlPrincipal __obj = new frm_Principal_pnlPrincipal();
	public static LatLng focoOcorrencia = null;
	private LinearLayout layout;
	private GoogleMap googleMap;
	public MapView mapView;
	private Bundle mapViewBundle;
	private ListenerRegistration listenerOcorrencias;
	private List<Ocorrencia> ocorrenciasCarregadas;
	private boolean mostrarSolucionados = false;
	private String textoBusca = "";
	private boolean buscaConfigurada = false;

	@Override
	public LinearLayout getLayout()
	{
		if (layout == null)
		{
			LayoutInflater inflater = LayoutInflater.from(RvActivity.__activity);
			layout = (LinearLayout) inflater.inflate(R.layout.frm_principal_pnlprincipal, null);
		}
		return layout;
	}

	@Override
	public void OnActivated()
	{
		// Criar novo Bundle se necessário
		if (mapViewBundle == null)
		{
			mapViewBundle = new Bundle();
		}

		mapView = layout.findViewById(R.id.mapView);
		mapView.onCreate(new Bundle());
		mapView.getMapAsync(this);

		EditText etSearch = layout.findViewById(R.id.etSearch);
		ImageView ivNotification = layout.findViewById(R.id.ivNotification);
		ImageView ivFeed = layout.findViewById(R.id.ivFeed);
		ImageView ivMap = layout.findViewById(R.id.ivMap);
		ImageView ivAdd = layout.findViewById(R.id.ivAdd);
		ImageView ivProfile = layout.findViewById(R.id.ivProfile);

		RadioGroup rgStatus = layout.findViewById(R.id.rgStatus);
		rgStatus.setOnCheckedChangeListener((group, checkedId) -> {
			mostrarSolucionados = (checkedId == R.id.rbSolucionados);
			renderizarMarcadores();
		});

		if (!buscaConfigurada)
		{
			etSearch.addTextChangedListener(new TextWatcher()
			{
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) { }

				@Override
				public void afterTextChanged(Editable s)
				{
					textoBusca = s.toString().trim().toLowerCase();
					renderizarMarcadores();
				}
			});
			buscaConfigurada = true;
		}

		ivNotification.setOnClickListener(v -> {
			// Ação da notificação
		});

		ivMap.setOnClickListener(v -> {
			// Ação do mapa
		});

		ivFeed.setOnClickListener(v -> {
			if (frm_Principal_pnlFeed.__obj == null)
				frm_Principal_pnlFeed.__obj = new frm_Principal_pnlFeed();

			frm_Principal_pnlFeed.__obj.Show();
			this.Hide();
		});

		ivAdd.setOnClickListener(v -> {

			if(frm_Principal_pnlOcorrencia.__obj == null)
				frm_Principal_pnlOcorrencia.__obj = new frm_Principal_pnlOcorrencia();

			frm_Principal_pnlOcorrencia.__obj.Show();
			frm_Principal_pnlOcorrencia.__obj.ShowCustom();
			this.Hide();
		});

		ivProfile.setOnClickListener(v -> {
			if(frm_Perfil_pnlPrincipal.__obj == null)
				frm_Perfil_pnlPrincipal.__obj = new frm_Perfil_pnlPrincipal();

			frm_Perfil_pnlPrincipal.__obj.Show();
			frm_Perfil_pnlPrincipal.__obj.ShowCustom();
			this.Hide();
		});
	}

	// Método de retorno do mapa pronto
	@SuppressLint("MissingPermission")
	@Override
	public void onMapReady(GoogleMap map)
	{
		this.googleMap = map;
		
		if (PermissaoManager.possuiPermissaoLocalizacao() &&
			ContextCompat.checkSelfPermission(RvActivity.__activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
		{
			this.googleMap.setMyLocationEnabled(true);
		}

		if (focoOcorrencia != null)
		{
			this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(focoOcorrencia, 17f));
			focoOcorrencia = null;
		}
		else if (CmPosicao.posicaoAtual != null)
		{
			LatLng minhaPosicao = new LatLng(CmPosicao.posicaoAtual.latitude, CmPosicao.posicaoAtual.longitude);
			this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaPosicao, 15f));
		}

		carregarMarcadores();
	}

	private void carregarMarcadores()
	{
		if (listenerOcorrencias != null)
			return;

		listenerOcorrencias = FirebaseOcorrencia.observarTodas(new FirebaseOcorrencia.CallbackListagem()
		{
			@Override
			public void onSucesso(List<Ocorrencia> lista)
			{
				ocorrenciasCarregadas = lista;
				renderizarMarcadores();
			}

			@Override
			public void onErro(Exception e)
			{
			}
		});
	}

	private void renderizarMarcadores()
	{
		if (googleMap == null || ocorrenciasCarregadas == null)
			return;

		googleMap.clear();

		for (Ocorrencia ocorrencia : ocorrenciasCarregadas)
		{
			if (ocorrencia.latitude == 0 && ocorrencia.longitude == 0)
				continue;

			boolean resolvida = CmConstantes.STATUS_RESOLVIDA.equals(ocorrencia.status);
			if (resolvida != mostrarSolucionados)
				continue;

			if (!textoBusca.isEmpty() && !correspondeBusca(ocorrencia))
				continue;

			googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(ocorrencia.latitude, ocorrencia.longitude))
					.title(ocorrencia.titulo)
					.snippet(CmConstantes.rotuloStatus(ocorrencia.status) + " • " + ocorrencia.categoria)
					.icon(iconePorCategoria(ocorrencia.categoria)));
		}
	}

	@Override
	public void OnDisabled()
	{
		if (listenerOcorrencias != null)
		{
			listenerOcorrencias.remove();
			listenerOcorrencias = null;
		}
	}

	private boolean correspondeBusca(Ocorrencia ocorrencia)
	{
		if (ocorrencia.titulo != null && ocorrencia.titulo.toLowerCase().contains(textoBusca))
			return true;

		if (ocorrencia.descricao != null && ocorrencia.descricao.toLowerCase().contains(textoBusca))
			return true;

		if (ocorrencia.bairro != null && ocorrencia.bairro.toLowerCase().contains(textoBusca))
			return true;

		if (ocorrencia.categoria != null && ocorrencia.categoria.toLowerCase().contains(textoBusca))
			return true;

		return false;
	}

	private BitmapDescriptor iconePorCategoria(String categoria)
	{
		int resId;

		if (categoria == null)
			resId = R.drawable.ic_outros;
		else
			switch (categoria)
			{
				case "BURACO": resId = R.drawable.ic_buraco; break;
				case "ILUMINACAO": resId = R.drawable.ic_iluminacao; break;
				case "LIXO": resId = R.drawable.ic_lixo; break;
				case "ARVORE": resId = R.drawable.ic_arvore; break;
				case "OBRA": resId = R.drawable.ic_obra; break;
				default: resId = R.drawable.ic_outros; break;
			}

		Bitmap original = BitmapFactory.decodeResource(RvActivity.__activity.getResources(), resId);
		Bitmap escalado = Bitmap.createScaledBitmap(original, 96, 96, false);

		return BitmapDescriptorFactory.fromBitmap(escalado);
	}

}