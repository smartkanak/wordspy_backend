package date.oxi.spyword.model;


public enum RoundState {
    WAITING_FOR_PLAYERS, // Round has not started yet. Waiting for players to join.
    PLAYERS_EXCHANGE_WORDS, // Players are taken at random and say a word to go with their word.
    VOTING_TO_END_GAME, // Players are voting on whether they want the round to end or continue.
    VOTING_FOR_SPY, // Players are voting on who they think is the spy.
    NO_MAJORITY_SPY_VOTES, // There was no majority vote for the spy.
    SPY_GUESS_WORD, // The spy is guessing the word.
    SPY_WON, // The spy guessed the word or didn't get blamed.
    GOOD_TEAM_WON, // The good team guessed the spy without the spy guessing the word.
}