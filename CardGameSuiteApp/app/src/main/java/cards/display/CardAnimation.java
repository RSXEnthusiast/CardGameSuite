package cards.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import cards.games.Fives;
import cards.games.Solitaire;

public class CardAnimation {

    final Card cardToAnimate;
    final boolean callPostAnimation;
    private Context context;

    public CardAnimation(Card card, boolean callPostAnimation, Context context) {
        this.cardToAnimate = card;
        this.callPostAnimation = callPostAnimation;
        this.cardToAnimate.animate().alpha(0f);
        this.cardToAnimate.setVisibility(View.VISIBLE);
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public void cardAnimate(float xStart, float xEnd, float yStart, float yEnd) {
        cardToAnimate.setX(0);
        cardToAnimate.setY(0);
        Animation slide = new TranslateAnimation(xStart, xEnd, yStart, yEnd);
        slide.setDuration(getAnimationSpeed());
        slide.setFillAfter(true);
        this.cardToAnimate.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation a) {
                cardToAnimate.animate().alpha(1f);
            }

            public void onAnimationRepeat(Animation a) {
            }

            public void onAnimationEnd(Animation a) {
                cardToAnimate.animate().alpha(0f).setDuration(0);
                if (callPostAnimation) {
                    if(context.toString().contains("Fives"))
                        Fives.postAnimation();
                    else if(context.toString().contains("Solitaire")){
                        Solitaire.postAnimation();
                    }

                }
            }
        });
    }

    public Card getCard() {
        return this.cardToAnimate;
    }

    public int getAnimationSpeed() {
        return 500*(-1*context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getInt("animationSpeed", 0)+6);
    }
}
