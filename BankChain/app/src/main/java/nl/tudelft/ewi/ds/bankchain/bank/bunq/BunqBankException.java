package nl.tudelft.ewi.ds.bankchain.bank.bunq;

import android.support.annotation.NonNull;

import nl.tudelft.ewi.ds.bankchain.bank.Bank;
import nl.tudelft.ewi.ds.bankchain.bank.BankException;
import nl.tudelft.ewi.ds.bankchain.bank.bunq.api.ApiError;
import retrofit2.HttpException;

/**
 * An exception wrapper.
 *
 * Wraps the Retrofit http exception and provides tools to get
 * the most useful information from both Retrofit, Bunq implementation
 * and the Bunq server.
 *
 * Problem this class solves is the need to convert the http exception
 * response body content into a POJO. This is done by the ApiError class.
 * CompletableFutures cant rethrow exceptions once they are caught, so
 * we have to skip the catching part and do the conversion early.
 * Also, the implementation details of the Bunq error has to be abstracted
 * away.
 *
 * @author Jos Kuijpers
 */
public class BunqBankException extends BankException {
    static final long serialVersionUID = -3387983567928367948L;

    private HttpException httpException;
    private ApiError error;

    public BunqBankException(HttpException httpEx) {
        this.httpException = httpEx;
    }

    /**
     * Set the bank.
     *
     * This is needed for converting the HTTP data into a proper error
     * using a converter.
     * When the BankException is thrown, the bank is not available.
     * Another 'solution' is to make a bank a singleton.
     *
     * @param bank bank object
     */
    @Override
    public void setBank(@NonNull Bank bank) {
        if (!(bank instanceof BunqBank)) {
            throw new IllegalArgumentException("Bank is not of type Bunq");
        }

        this.error = ApiError.parseError((BunqBank) bank, httpException);
    }

    HttpException getHttpException() {
        return httpException;
    }

    @Override
    public String getMessage() {
        if (error == null) {
            return httpException.getMessage();
        }

        return error.getError();
    }

    @Override
    public String getLocalizedMessage() {
        if (error == null) {
            return httpException.getLocalizedMessage();
        }

        return error.getLocalizedError();
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return httpException.getStackTrace();
    }

    @Override
    public synchronized Throwable getCause() {
        return httpException.getCause();
    }

    @Override
    @SuppressWarnings("PMD")
    public void printStackTrace() {
        httpException.printStackTrace();
    }
}
