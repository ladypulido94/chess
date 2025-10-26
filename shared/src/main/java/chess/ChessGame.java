package chess;

import java.util.ArrayList;
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null){
            return null;
        }

        //Get all the possible moves the piece can make
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);

        //For each possible move, check if it leaves the King in check
        for(ChessMove move: possibleMoves){
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            //Make the move
            board.addPiece(move.getEndPosition(), movingPiece);
            board.addPiece(move.getStartPosition(), null);

            //Check if this leave the king in check
            if(!isInCheck(movingPiece.getTeamColor())){

                //Add to valid moves if the king is safe
                validMoves.add(move);
            }

            //Undo the move
            board.addPiece(move.getStartPosition(), movingPiece);
            board.addPiece(move.getEndPosition(), capturedPiece);

        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition position = move.getStartPosition();
        ChessPiece pieceAtPosition = board.getPiece(position);

        //Piece at start position
        if(pieceAtPosition == null){
            throw new InvalidMoveException();
        }

        //It's the right team turns
        ChessGame.TeamColor color = getTeamTurn();
        if(pieceAtPosition.getTeamColor() != color){
            throw new InvalidMoveException();
        }

        //The move is in the piece's valid move
        Collection<ChessMove> validMoves = validMoves(position);
        if(!validMoves.contains(move)){
            throw new InvalidMoveException();
        }

        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        board.addPiece(move.getEndPosition(), movingPiece);

        //Check the Pawn promotion
        if(move.getPromotionPiece() != null){
            ChessPiece promotionPiece = new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        }

        board.addPiece(move.getStartPosition(), null);

        //Change Turns
        if(getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }


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

        //Find the King
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){

                //Get the piece at this position
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(piecePosition);

                //Check if it's a King and the right color
                if(pieceAtPosition != null && pieceAtPosition.getPieceType() == ChessPiece.PieceType.KING
                        && pieceAtPosition.getTeamColor() == teamColor){

                    //Save the position
                    kingPosition = piecePosition;
                }
            }

        }

        //Check if an enemy piece can attack the King
        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition piecePosition = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(piecePosition);

                if(pieceAtPosition != null && pieceAtPosition.getTeamColor() != teamColor){

                    //Get their possibles moves
                    Collection<ChessMove> moves = pieceAtPosition.pieceMoves(board, piecePosition);

                    for(ChessMove move: moves){
                        //Check if the move can capture the king
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
        //Are the in check?
        if(!isInCheck(teamColor)){
            return false;
        }

        //Do they have any legal moves?
        for(int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(position);

                //Get all possible moves
                if(pieceAtPosition != null && pieceAtPosition.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);

                    //See if it gets them out of check
                    if(!moves.isEmpty()){
                        return false;
                    }
                }

            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            return false;
        }

        for(int row = 1; row <= 8; row++){
            for(int col = 1; col <= 8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece pieceAtPosition = board.getPiece(position);

                if(pieceAtPosition != null && pieceAtPosition.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(position);

                    if(!moves.isEmpty()){
                        return false;
                    }
                }
            }
        }

        return true;
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
