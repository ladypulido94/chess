package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RookMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        int[][] directions = {
                {0,1},{0,-1},{-1,0},{1,0}
        };

        //Loop through each direction
        for (int[] direction: directions){
            int rowDelta = direction[0];
            int colDelta = direction[1];

            //New position of the Rook
            int newRookRow = position.getRow() + rowDelta;
            int newRookCol = position.getColumn() + colDelta;

            //Checking if the rook can move or not
            while(isValidPosition(newRookRow, newRookCol)){
                ChessPosition newPosition = new ChessPosition(newRookRow, newRookCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                //The square is empty. Add the move
                if (pieceAtNewPosition == null){
                    ChessMove newMove = new ChessMove(position,newPosition, null);
                    moves.add(newMove);
                }
                //The square has an enemy piece. Add the move and stop
                else if(pieceAtNewPosition.getTeamColor() != color){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                    break;
                }
                //The square has team piece. Stop
                else {
                    break;
                }

                //Move to the next square
                newRookRow = newRookRow + rowDelta;
                newRookCol = newRookCol + colDelta;
            }

        }

        return moves;
    }

    private boolean isValidPosition (int row, int col){
        return row >=1 && row <= 8 && col >= 1 && col <= 8;
    }
}
