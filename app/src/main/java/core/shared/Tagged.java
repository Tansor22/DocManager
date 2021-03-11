package core.shared;

public interface Tagged {
    default String getTag() {
        return this.getClass().getSimpleName();
    }
}
