package obj.cidademais.Core;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import obj.cidademais.RvActivity;

public class CmUi
{
	public static void arredondar(View view, int raioDp)
	{
		float densidade = view.getResources().getDisplayMetrics().density;
		float raioPx = raioDp * densidade;

		view.setClipToOutline(true);
		view.setOutlineProvider(new ViewOutlineProvider()
		{
			@Override
			public void getOutline(View v, Outline outline)
			{
				outline.setRoundRect(0, 0, v.getWidth(), v.getHeight(), raioPx);
			}
		});
	}

	// Abre a imagem em tela cheia, inteira (sem corte), sobre fundo preto. Toque fecha.
	public static void mostrarImagemTelaCheia(String url)
	{
		Dialog dialog = new Dialog(RvActivity.__activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

		ImageView iv = new ImageView(RvActivity.__activity);
		iv.setBackgroundColor(Color.BLACK);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
		iv.setAdjustViewBounds(false);
		iv.setOnClickListener(v -> dialog.dismiss());

		dialog.setContentView(iv);
		dialog.show();

		Glide.with(RvActivity.__activity).load(url).into(iv);
	}
}
