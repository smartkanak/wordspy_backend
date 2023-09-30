package date.oxi.spyword.model;


public enum RoundState {
    WAITING_FOR_PLAYERS, // Round has not started yet. Waiting for players to join.
    PLAYERS_EXCHANGE_WORDS, // Players are taken at random and say a word to go with their word.
    VOTING_TO_END_GAME, // Players are voting on whether they want the round to end or continue.
    VOTING_FOR_SPY, // Players are voting on who they think is the spy.
}