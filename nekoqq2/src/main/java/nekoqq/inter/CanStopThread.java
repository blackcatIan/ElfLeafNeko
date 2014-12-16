package nekoqq.inter;

public interface CanStopThread {
    
    /**
     * set thread continue flag to be false to stop thread
     */
    public void cancel();
}
