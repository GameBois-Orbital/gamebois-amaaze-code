package com.gamebois.amaaze.graphics;

import android.content.Context;
import android.util.Log;

import com.gamebois.amaaze.view.GameActivity;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

public class JBox2DContactListener implements ContactListener {
    private String LOG_TAG = GameActivity.class.getSimpleName();
    private boolean gameOver, isWarping;
    public String touched;

    public JBox2DContactListener(Context context) {
        this.gameOver = false;
        this.isWarping = false;
    }

    public boolean isWarping() {
        return isWarping;
    }

    public void notWarping() {
        this.isWarping = false;
    }


    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        Object o1 = bodyA.getUserData();
        Object o2 = bodyB.getUserData();

        if ((o1.getClass() == Ball2D.class && o2.getClass() == End2D.class) ||
                (o1.getClass() == End2D.class && o2.getClass() == Ball2D.class)) {
            gameOver = true;
            Log.d(LOG_TAG, "End Contact detected");
        }
        if ((o1.getClass() == Integer.class && o2.getClass() == Ball2D.class )
                || (o2.getClass() == Integer.class && o1.getClass() == Ball2D.class)) {
            isWarping = true;
            if (o1.getClass() == Integer.class) {
                touched = o1.toString();
            } else {
                touched = o2.toString();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
