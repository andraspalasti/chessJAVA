package chess.core;

public class Move {
    public final Square from, to;

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        Move other = (Move) obj;
        return this.to.equals(other.to) && this.from.equals(other.from);
    }

    @Override
    public String toString() {
        return from + " -> " + to;
    }
}
