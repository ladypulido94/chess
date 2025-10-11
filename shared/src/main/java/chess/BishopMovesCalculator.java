package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();

        //Up-right = (1,1)
        //Up-left = (1,-1)
        //Down-right = (-1,1)
        //Down-left = (-1,-1)
        int[][] directions = {
                {1,1},{-1,1},{-1,-1},{1,-1}
        };

        //TO DO: Loop trough each direction
        for (int[] direction : directions) {
            int rowDelta = direction[0];
            int colDelta = direction[1];

            //The new position for the Bishop
            int newBishopRow = position.getRow() + rowDelta;
            int newBishopCol = position.getColumn() + colDelta;

            //For each direction, keep moving until you hit something
            while (isValidPosition(newBishopRow, newBishopCol)) {

                ChessPosition newPosition = new ChessPosition(newBishopRow, newBishopCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

                //The new position in the board is null meaning there is no piece
                if (pieceAtNewPosition == null){
                    //Add the piece
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);

                }
                //The color of the Bishop is not the same as the piece in the board
                //This is an enemy piece. You add the move and then stop.
                else if (pieceAtNewPosition.getTeamColor() != color) {
                    //Add the piece
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                    break;

                }
                //The piece is from your same team. You stop
                else {
                    break;
                }

                //Move to the next square
                newBishopRow = newBishopRow + rowDelta;
                newBishopCol = newBishopCol + colDelta;
            }

        }

        return moves;
    }

    //This method checks if we are at the edge of the board
    private boolean isValidPosition (int row, int col) {
        return row >=1 && row <= 8 && col >=1 && col <= 8;
    }
}
