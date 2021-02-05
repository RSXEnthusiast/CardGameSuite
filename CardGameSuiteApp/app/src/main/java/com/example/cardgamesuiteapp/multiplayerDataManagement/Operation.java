package com.example.cardgamesuiteapp.multiplayerDataManagement;

import java.io.Serializable;

public enum Operation{
	deal, playerDraw, playerDrawIntoIndex, playerDrawFromDiscard, playerDrawIntoIndexFromDiscard, discardByValue,
	discardByIndex, discardFromDeck, initialize, recover, shuffle,nextPlayer,flipOverCard
}
