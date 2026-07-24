package obj.cidademais;

import android.os.Handler;
import android.widget.LinearLayout;

public abstract class RvView
{
	public abstract LinearLayout getLayout();

	public void OnActivated()
	{
	}

	public void OnDisabled()
	{
	}

	// Ciclo de vida (opcional de sobrescrever)
	public void onResume()
	{
	}

	public void onPause()
	{
	}

	public void onStart()
	{
	}

	public void onStop()
	{
	}

	public void onDestroy()
	{
	}

	public void onLowMemory()
	{
	}

	public void Show()
	{
		synchronized (this)
		{
			RvActivity.mostrarCarregando("Carregando...");

			int index = RvActivity.__views.indexOf(this);
			if (index != -1 && index == RvActivity.__views.size() - 1)
				return;
			if (index != -1)
				RvActivity.__views.remove(index);

			if (!RvActivity.__views.isEmpty())
			{
				RvView topView = RvActivity.__views.get(RvActivity.__views.size() - 1);
				topView.OnDisabled();
			}

			RvActivity.__views.add(this);
			LinearLayout layout = RvActivity.criaTela(this);
			RvActivity.__activity.setContentView(layout);

			new Handler().postDelayed(() -> {
				this.OnActivated();
				RvActivity.esconderCarregando();
			}, 250);
		}
	}

	public void Hide()
	{

		int index = RvActivity.__views.indexOf(this);
		if (index != -1 && index == RvActivity.__views.size() - 1)
		{
			RvActivity.__views.remove(index);
			if (!RvActivity.__views.isEmpty())
			{
				RvView previous = RvActivity.__views.get(RvActivity.__views.size() - 1);
				RvActivity.__activity.setContentView(previous.getLayout());
				previous.OnActivated();
			}
		}
		else
			if (index != -1)
			{
				RvActivity.__views.remove(index);
			}
	}
}