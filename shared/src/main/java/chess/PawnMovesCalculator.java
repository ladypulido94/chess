package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece piece = board.getPiece(position);

        //Calculate direction for white or black pawn
        int direction = 0;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            direction = 1;
        } else {
            direction = -1;
        }

        //Calculate the initial row for white or black pawn
        int startRow = 0;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            startRow = 2;
        } else {
            startRow = 7;
        }

        //Calculate the promotion for white or black pawn
        int promotionRow = 0;
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            promotionRow = 8;
        } else {
            promotionRow = 1;
        }

        //FORWARD 1 SQUARE
        //Get current row and column
        int currentRow = position.getRow();
        int currentCol = position.getColumn();

        //Get new position for moving forward 1 square
        int newPawnRow = currentRow + direction;
        int newPawnCol = currentCol;

        //Checking if the square didn't pass the edge of the board
        if(isValidPosition(newPawnRow, newPawnCol)){
            ChessPosition newPosition = new ChessPosition(newPawnRow, newPawnCol);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

            //The square is empty. Add the move
            if(pieceAtNewPosition == null){
                addMoveWithPromotion(moves, position, newPosition, newPawnRow, promotionRow);
            }

            //FORWARD 2 SQUARES

            //Checking to see if the Pawn is in the starting row
            if(currentRow == startRow){
                int twoSquareRow = currentRow + (2*direction);
                int twoSquareCol = currentCol;

                ChessPosition newTwoSquarePosition = new ChessPosition(twoSquareRow, twoSquareCol);
                ChessPiece twoSquarePieceAtNewPosition = board.getPiece(newTwoSquarePosition);

                //Checking that the 1st and 2nd square are empty
                if(pieceAtNewPosition == null && twoSquarePieceAtNewPosition == null){
                    addMoveWithPromotion(moves, position, newTwoSquarePosition, twoSquareRow, promotionRow);
                }
            }

            //CAPTURE
            //Left Diagonal
            int leftRow = currentRow + direction;
            int leftCol = currentCol - 1;

            if(isValidPosition(leftRow, leftCol)){
                ChessPosition leftDiagonalPosition = new ChessPosition(leftRow, leftCol);
                ChessPiece leftDiagonalPiece = board.getPiece(leftDiagonalPosition);

                //Checking that there is an enemy piece to add the move
                if(leftDiagonalPiece != null && piece.getTeamColor() != leftDiagonalPiece.getTeamColor()){
                    addMoveWithPromotion(moves, position, leftDiagonalPosition, leftRow, promotionRow);
                }
            }

            //Right Diagonal
            int rightRow = currentRow + direction;
            int rightCol = currentCol + 1;

            ChessPosition rightDiagonalPosition = new ChessPosition(rightRow, rightCol);
            ChessPiece rightDiagonalPiece = board.getPiece(rightDiagonalPosition);

            //Checking that there is an enemy piece to add the move
            if(rightDiagonalPiece != null && piece.getTeamColor() != rightDiagonalPiece.getTeamColor()){
                addMoveWithPromotion(moves, position, rightDiagonalPosition, rightRow, promotionRow);
            }

        }

        return moves;
    }

    private boolean isValidPosition(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private void addMoveWithPromotion(Collection<ChessMove> moves, ChessPosition start, ChessPosition end,
                                      int endRow, int promotionRow){
        //Edge of the board
        if(endRow == promotionRow){
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));

        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}
