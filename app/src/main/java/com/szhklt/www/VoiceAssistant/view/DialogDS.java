package com.szhklt.www.VoiceAssistant.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.szhklt.www.VoiceAssistant.R;


public class DialogDS extends Dialog {

	public DialogDS(Context context) {
		super(context);
	}

	public DialogDS(Context context, int theme) {
		super(context, theme);
	}

	@SuppressLint("WrongViewCast")
	public static class Builder {
		private Context context;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private DialogInterface.OnClickListener positiveButtonClickListener;
		private DialogInterface.OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}


		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		@SuppressLint("WrongViewCast")
		@SuppressWarnings("deprecation")
		public DialogDS create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final DialogDS dialog = new DialogDS(context, R.style.yorkdialog);
			View layout = inflater.inflate(R.layout.dialog_ds, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			if (positiveButtonText != null) {
				((TextView) layout.findViewById(R.id.okok))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.okok))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.okok).setVisibility(
						View.GONE);
			}
			if (negativeButtonText != null) {
				((TextView) layout.findViewById(R.id.nono))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((TextView) layout.findViewById(R.id.nono))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									negativeButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {

				layout.findViewById(R.id.nono).setVisibility(
						View.GONE);
			}

			if (message != null) {
				((TextView) layout.findViewById(R.id.tiptext)).setText(message);
			} else if (contentView != null) {

			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
