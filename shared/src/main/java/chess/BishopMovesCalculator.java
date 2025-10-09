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
                {1,1},{1,-1},{-1,1},{-1,-1}
        };

        //TO DO: Loop trough each direction
        for (int[] direction : directions) {
            int rowDelta = direction[0];
            int colDelta = direction[1];

            //The new position for the Bishop
            int newBishopRow = position.getRow() + rowDelta;
            int newBishopCol = position.getColumn() + colDelta;

            //TO DO: For each direction, keep moving until you hit something
            while (isValidPosition(newBishopRow, newBishopCol)) {
                //TO DO: Check what's at this square
                //TO DO: Decide if we can move there
                //TO DO: Add the move
                //TO DO: Decide if we should we keep going or stop
                ChessPiece pieceAtNewPosition = board.getPiece(new ChessPosition(newBishopRow, newBishopCol));
                ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

                //The new place in the board is null
                if (pieceAtNewPosition == null){
                    //Add the piece
                }
                //The color of the Bishop is not the same as the piece in the board
                else if (pieceAtNewPosition.getTeamColor() != color) {
                    //Add the piece

                } else {
                    //Do nothing
                }


                newBishopRow = newBishopRow + rowDelta;
                newBishopCol = newBishopCol + colDelta;
            }

        }

        return moves;
    }

    //TO DO: Add valid moves to the collection
    //This method checks if we are at the edge of the board
    private boolean isValidPosition (int row, int col) {
        return row >=1 && row <= 8 && col >=1 && col <= 8;
    }
}
