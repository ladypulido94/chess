package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor color = board.getPiece(position).getTeamColor();

        int[][] directions = {
                {2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2},{2,-1}
        };

        //Loop through all the directions
        for (int[] direction: directions){
            int rowDelta = direction[0];
            int colDelta = direction[1];

            //The new position for the Knight
            int newKnightRow = position.getRow() + rowDelta;
            int newKnightCol = position.getColumn() + colDelta;

            //Check if the position the knight is going to land is valid
            if (isValidPosition(newKnightRow, newKnightCol)){
                ChessPosition newPosition = new ChessPosition(newKnightRow, newKnightCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                //The square is empty. Add a new move
                if(pieceAtNewPosition == null){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                }
                //The square has an enemy piece. Add a new move
                else if (pieceAtNewPosition.getTeamColor() != color){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                }
            }

        }
        return moves;
    }

    private boolean isValidPosition (int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
