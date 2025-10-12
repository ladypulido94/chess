package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator{
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

            //The new position of the King
            int newKingRow = position.getRow() + rowDelta;
            int newKingCol = position.getColumn() + colDelta;

            //Checking if the square didn't pass the edge of the board
            if(isValidPosition(newKingRow, newKingCol)){
                ChessPosition newPosition = new ChessPosition(newKingRow, newKingCol);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                //The square is empty. Add a new move
                if(pieceAtNewPosition == null){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                }
                //The square has an enemy piece. Add a new move
                else if(pieceAtNewPosition.getTeamColor() != color){
                    ChessMove newMove = new ChessMove(position, newPosition, null);
                    moves.add(newMove);
                }

            }

        }
        return moves;
    }

    private boolean isValidPosition(int row, int col){
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}
