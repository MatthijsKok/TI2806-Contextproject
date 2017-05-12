package nl.tudelft.ewi.ds.bankchain.bank.bunq.api;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.ewi.ds.bankchain.bank.bunq.BunqBank;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;
import retrofit2.Response;

/**
 * Api error format
 *
 * @author Jos Kuijpers
 */
public class ApiError {
    @SerializedName("Error")
    List<Error> errors;

    String status;
    int statusCode;

    private ApiError(String description, String localizedDescription) {
        errors = new ArrayList<>();

        Error err = new Error();
        err.description = description;
        err.localizedDescription = localizedDescription;
        errors.add(err);

        status = "No Status";
    }

    @Override
    public String toString() {
        return getError();
    }

    /**
     * Get the error message
     *
     * @return string with error or null
     */
    public String getError() {
        if (errors.size() > 0) {
            return errors.get(0).toString();
        }

        return null;
    }

    /**
     * Get an error message for display purposes.
     *
     * @return localized error string
     */
    public String getLocalizedError() {
        if (errors.size() > 0) {
            Error err = errors.get(0);

            return err.localizedError();
        }

        return null;
    }

    /**
     * Parse a throwable into an Error response.
     *
     * If given throwable is not an HttpException, an ErrorResponse is created
     * with the description of the throwable.
     *
     * @param bank Bank
     * @param obj Object
     * @return Error response
     */
    @NonNull
    public static ApiError parseError(BunqBank bank, Throwable obj) {
        if (obj instanceof HttpException) {
            return ApiError.parseError(bank, ((HttpException) obj).response());
        }

        // Often, the http exception is contained by an interrupt, completion or other functional
        // exception.
        if (obj.getCause() instanceof HttpException) {
            return ApiError.parseError(bank, ((HttpException) obj.getCause()).response());
        }

        return new ApiError(obj.getMessage(), obj.getLocalizedMessage());
    }

    /**
     * Parse a response into an error
     *
     * @param bank bank
     * @param response response
     * @return error response
     */
    @NonNull
    public static ApiError parseError(BunqBank bank, Response<?> response) {
        Converter<ResponseBody, ApiError> converter;

        converter = bank.getRetrofit().responseBodyConverter(ApiError.class, new Annotation[0]);

        try {
            ApiError er = converter.convert(response.errorBody());
            er.status = response.message();
            er.statusCode = response.code();

            return er;
        } catch (IOException e) {
            return new ApiError(e.getMessage(), e.getLocalizedMessage());
        }
    }

    /**
     * Error object
     */
    public class Error {
        @SerializedName("error_description")
        String description;

        @SerializedName("error_description_translated")
        String localizedDescription;

        @Override
        public String toString() {
            return description;
        }

        /**
         * Get an error message for display purposes.
         *
         * @return localized error string
         */
        public String localizedError() {
            return localizedDescription == null ? description : localizedDescription;
        }
    }
}
