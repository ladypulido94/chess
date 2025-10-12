package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        int[][] directions = {
                {1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}
        };

        for(int[] direction: directions){
            int rowDelta = direction[0];
            int colDelta = direction[1];

            //The new position for the Queen
            int newQueenRow = position.getRow() + rowDelta;
            int newQueenCol = position.getColumn() + colDelta;

            //Checking if the square didn't pass the edge of the board
            while(isValidPosition(newQueenRow, newQueenCol)){
                ChessPosition newPosition = new ChessPosition(newQueenRow, newQueenCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                //The square is empty. Add new move
                if(pieceAtNewPosition == null){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                }
                //The square has an enemy piece. Add new move and stop
                else if (pieceAtNewPosition.getTeamColor() != color) {
                    ChessMove newMove = new ChessMove(position, newPosition,null);
                    moves.add(newMove);
                    break;
                }
                //The square has a team piece. Stop
                else {
                    break;
                }

                //Moving to the next position
                newQueenRow = newQueenRow + rowDelta;
                newQueenCol = newQueenCol + colDelta;
            }
        }
        return moves;
    }

    private boolean isValidPosition(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
