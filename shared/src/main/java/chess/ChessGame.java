package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {

        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the King's position
        ChessPosition kingPosition = null;
        //TO DO: Loop though the board to find the King
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                //TO DO: Get the piece at this position
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(piecePosition);

                //TO DO: Check if it's a King and the right color
                if(pieceAtPosition != null && pieceAtPosition.getPieceType() == ChessPiece.PieceType.KING
                        && pieceAtPosition.getTeamColor() == teamColor){
                    //TO DO: If yes, save the position
                    kingPosition = piecePosition;
                }
            }

        }

        //Check if an enemy piece can attack the King
        //TO DO:Loop through the board and find the enemy pieces
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(piecePosition);

                //TO DO: Check if it's an enemy piece
                if(pieceAtPosition != null && pieceAtPosition.getTeamColor() != teamColor){
                    //TO DO: If yes, get their possibles moves
                    Collection<ChessMove> moves = pieceAtPosition.pieceMoves(board, piecePosition);

                    for(ChessMove move: moves){
                        //TO DO: Check if the move can capture the king
                        if(move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }

            }
        }

        return false; //If it gets here, the King is not in check
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {

        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {

        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
