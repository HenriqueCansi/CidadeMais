package obj.cidademais.Core;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

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
}
