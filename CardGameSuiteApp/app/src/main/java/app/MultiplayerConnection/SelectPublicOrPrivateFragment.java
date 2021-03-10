package app.MultiplayerConnection;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cardgamesuiteapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

import static android.graphics.Color.BLACK;

public class SelectPublicOrPrivateFragment extends MultiplayerWaitingRoomActivityFragment {


    private static final String TAG = SelectPublicOrPrivateFragment.class.getSimpleName();


    public SelectPublicOrPrivateFragment() {
        super(R.layout.mpconnection_fragment_select_public_or_private_buttons);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _MultiPlayerConnector.FullReset();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _joinPrivateButton = view.findViewById(R.id.privateButton);
        _joinPublicButton = view.findViewById(R.id.publicButton);

        _joinPrivateButton.setOnClickListener(v -> privateGameSelected());
        _joinPublicButton.setOnClickListener(v -> toPublicGameWaitingRoomFragment());

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        @ColorInt int primaryTextColor = typedValue.data;

        int[] buttonTextColors = new int[]{
                primaryTextColor,
                Color.LTGRAY,
                BLACK,
                BLACK
        };

        ColorStateList buttonTextColorStateList = new ColorStateList(buttonColorStates, buttonTextColors);

        TypedValue backGroundColorTypeValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimaryVariant, backGroundColorTypeValue, true);
        @ColorInt int backGroundColor = backGroundColorTypeValue.data;

        int[] buttonBackgroundColors = new int[]{
                backGroundColor,
                Color.GRAY,
                BLACK,
                BLACK
        };

        ColorStateList buttonBackgroundColorStateList = new ColorStateList(buttonColorStates, buttonBackgroundColors);

        _joinPrivateButton.setTextColor(buttonTextColorStateList);
        _joinPrivateButton.setBackgroundTintList(buttonBackgroundColorStateList);
        _joinPublicButton.setTextColor(buttonTextColorStateList);
        _joinPublicButton.setBackgroundTintList(buttonBackgroundColorStateList);

        SetMultiPlayerConnectorObserver(_multiPlayerConnectorObserver);


    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            _MultiPlayerConnector.connectToServer();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        System.out.println("resuming SelectPublicOrPrivate");

        menuButtonsEnabledStatus(_MultiPlayerConnector.Connected());
    }


    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }


    private void privateGameSelected() {
        if (!ensureConnection()) {
            return;
        }
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PrivateGameOptionsFragment.class.getCanonicalName());

        _MultiplayerWaitingRoomActivity.changeFragment(PrivateGameOptionsFragment.class.getCanonicalName(),result);
    }

    private boolean ensureConnection() {

        if (_MultiPlayerConnector.Connected()) {
            return true;
        }

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            Snackbar.make(_MultiplayerWaitingRoomActivity.findViewById(R.id.multiPlayerWaitingRoomCoordinatorLayout), "Unable To Connect To Server...", Snackbar.LENGTH_LONG)
                    .show();
        });


        return false;
    }


    private void toPublicGameWaitingRoomFragment() {
        if (!ensureConnection()) {
            return;
        }
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PublicGameWaitingRoom.class.getCanonicalName());
        // The child fragment needs to still set the result on its parent fragment manager
        _MultiplayerWaitingRoomActivity.changeFragment(PublicGameWaitingRoom.class.getCanonicalName(),result);
        //getParentFragmentManager().setFragmentResult("changeFragment", result);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                _MultiPlayerConnector.Close();
                _MultiplayerWaitingRoomActivity.finish();

            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }


    private Observer _multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;

            if (!socketIOEventArg.CompareEventWatcher(TAG)) return;
            switch (socketIOEventArg._EventName) {

                case Socket.EVENT_CONNECT:
                case Socket.EVENT_DISCONNECT:
                case Socket.EVENT_CONNECT_ERROR:

                    _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
                        menuButtonsEnabledStatus(_MultiPlayerConnector.Connected());
                    });
                    break;
            }
        }
    };


    @Override
    public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(Socket.EVENT_CONNECT, args -> {
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(Socket.EVENT_CONNECT, TAG, args);
            multiPlayerConnector.notifyObservers(socketIOEventArg);
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {

            multiPlayerConnector.notifyObservers(new SocketIOEventArg(Socket.EVENT_CONNECT_ERROR, TAG, args));
        });
        socket.on(Socket.EVENT_DISCONNECT, args -> {

            multiPlayerConnector.notifyObservers(new SocketIOEventArg(Socket.EVENT_DISCONNECT, TAG, args));
        });


    }


    //----------------- ui elements

    MaterialButton _joinPrivateButton;
    MaterialButton _joinPublicButton;


    int[][] buttonColorStates = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };

    int[] buttonColors;

    ColorStateList buttonColorStateList;

    private void menuButtonsEnabledStatus(boolean isEnabled) {

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            _joinPrivateButton.setEnabled(isEnabled);
            _joinPublicButton.setEnabled(isEnabled);

        });
    }
}



