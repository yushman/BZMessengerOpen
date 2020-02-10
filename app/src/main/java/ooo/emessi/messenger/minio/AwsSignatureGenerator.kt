package ooo.emessi.messenger.minio

import uk.co.lucasweb.aws.v4.signer.HttpRequest
import uk.co.lucasweb.aws.v4.signer.Signer
import uk.co.lucasweb.aws.v4.signer.credentials.AwsCredentials
import java.net.URI
import java.text.DateFormat
import java.util.*

object AwsSignatureGenerator {

    private const val contentSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

//    @RequiresApi(26)
    fun generateSignature(
    url: String,
    host: String,
    aKey: String,
    sKey: String,
    rMethod: RequestMethod,
    dateFormatted: String
): String {
//        val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
//        val dateFormatted = OffsetDateTime.now(ZoneOffset.UTC).format(dateFormatter)
        val request = HttpRequest(rMethod.name, URI(url))
        return Signer.builder()
            .awsCredentials(AwsCredentials(aKey, sKey))
            .header("Host", host)
            .header("X-Amz-Content-Sha256", contentSha256)
            .header("X-Amz-Date", dateFormatted)
            .buildS3(request, contentSha256)
            .signature
    }

    enum class RequestMethod{
        GET,
        PUT,
        POST
    }
}