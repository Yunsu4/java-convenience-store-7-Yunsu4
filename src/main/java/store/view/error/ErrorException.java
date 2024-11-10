package store.view.error;

public class ErrorException extends IllegalArgumentException{
    public ErrorException(InputErrorType inputError){
        super(InputErrorType.ERROR_MESSAGE.getMessage()+inputError.getMessage());
    }

    public ErrorException(String errorMessage){
        super(InputErrorType.ERROR_MESSAGE.getMessage()+ errorMessage);
    }
}
