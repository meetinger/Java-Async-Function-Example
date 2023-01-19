import java.util.concurrent.Callable;
public class AsyncFunction {
    public enum Status{
        NOT_STARTED,
        RUNNING,
        DONE,
        INTERRUPT
    }
    private Callable<?> func;
    private Object result;
    private boolean await;
    private Thread thread;
    private Status status;
    public AsyncFunction(Callable<?> func, boolean await) {
        this.func = func;
        this.await = await;
        this.result = null;
        this.status = Status.NOT_STARTED;
    }

    public Object run(){
        Runnable task = ()->{
            try {
                this.result = func.call();
                this.status = Status.DONE;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        this.thread = new Thread(task);
        this.thread.setDaemon(true);
        thread.start();
        this.status = Status.RUNNING;
        if(await){
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return this.result;
    }
    public void interrupt(){
        this.thread.interrupt();
        this.status = Status.INTERRUPT;
    }
    public Object getResult(){
        return this.result;
    }
    public Status getStatus(){
        return this.status;
    }

    public boolean isAwait() {
        return await;
    }

    public void setAwait(boolean await) {
        if (this.status == Status.NOT_STARTED) {
            this.await = await;
            return;
        }
        throw new RuntimeException("Function already started!");
    }
}

