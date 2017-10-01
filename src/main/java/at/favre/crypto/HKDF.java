package at.favre.crypto;

import javax.crypto.Mac;
import java.io.ByteArrayOutputStream;


/**
 * A standards-compliant implementation of RFC 5869
 * for HMAC-based Key Derivation Function.
 * <p>
 * HKDF follows the "extract-then-expand" paradigm, where the KDF
 * logically consists of two modules.  The first stage takes the input
 * keying material and "extracts" from it a fixed-length pseudorandom
 * key K.  The second stage "expands" the key K into several additional
 * pseudorandom keys (the output of the KDF).
 * <p>
 * HKDF was first described by Hugo Krawczyk.
 *
 * @see <a href="https://tools.ietf.org/html/rfc5869">RFC 5869</a>
 * @see <a href="https://eprint.iacr.org/2010/264.pdf">Cryptographic Extraction and Key Derivation:
 * The HKDF Scheme</a>
 */
public final class HKDF {
    private HKDF() {
    }

    /* ************************************************************************** EXTRACT */

    /**
     * The first stage takes the input keying material and "extracts" from it a fixed-length pseudorandom
     * key K. The goal of the "extract" stage is to "concentrate" the possibly dispersed entropy of the input
     * keying material into a short, but cryptographically strong, pseudorandom key.
     * <p>
     * Uses Hmac with Sha256.
     *
     * @param inputKeyingMaterial data to be extracted (IKM)
     * @param salt                optional salt value (a non-secret random value);
     *                            if not provided, it is set to a array of hash length of zeros.
     * @return a new byte array pseudorandom key (of hash length in bytes) (PRK) which can be used to expand
     */
    public static byte[] extractHmacSha256(byte[] inputKeyingMaterial, byte[] salt) {
        return extract(HkdfMacFactory.Default.hmacSha256(), inputKeyingMaterial, salt);
    }

    /**
     * The first stage takes the input keying material and "extracts" from it a fixed-length pseudorandom
     * key K. The goal of the "extract" stage is to "concentrate" the possibly dispersed entropy of the input
     * keying material into a short, but cryptographically strong, pseudorandom key.
     * <p>
     * Uses Hmac with Sha512.
     *
     * @param inputKeyingMaterial data to be extracted (IKM)
     * @param salt                optional salt value (a non-secret random value);
     *                            if not provided, it is set to a array of hash length of zeros.
     * @return a new byte array pseudorandom key (of hash length in bytes) (PRK) which can be used to expand
     */
    public static byte[] extractHmacSha512(byte[] inputKeyingMaterial, byte[] salt) {
        return extract(HkdfMacFactory.Default.hmacSha512(), inputKeyingMaterial, salt);
    }

    /**
     * The first stage takes the input keying material and "extracts" from it a fixed-length pseudorandom
     * key K. The goal of the "extract" stage is to "concentrate" the possibly dispersed entropy of the input
     * keying material into a short, but cryptographically strong, pseudorandom key.
     *
     * @param macFactory          factory creating the used mac algorithm
     * @param inputKeyingMaterial data to be extracted (IKM)
     * @param salt                optional salt value (a non-secret random value);
     *                            if not provided, it is set to a array of hash length of zeros.
     * @return a new byte array pseudorandom key (of hash length in bytes) (PRK) which can be used to expand
     */
    public static byte[] extract(HkdfMacFactory macFactory, byte[] inputKeyingMaterial, byte[] salt) {
        return new Extractor(macFactory).hkdfExtract(inputKeyingMaterial, salt);
    }

    /* ************************************************************************** EXPAND */

    /**
     * The second stage "expands" the pseudorandom key to the desired
     * length; the number and lengths of the output keys depend on the
     * specific cryptographic algorithms for which the keys are needed.
     * <p>
     * Uses Hmac with Sha256.
     *
     * @param pseudoRandomKey a pseudorandom key of at least hmac hash length in bytes (usually, the output from the extract step)
     * @param info            optional context and application specific information; may be null
     * @param outLengthBytes  length of output keying material in bytes
     * @return new byte array of output keying material (OKM)
     */
    public static byte[] expandHmacSha256(byte[] pseudoRandomKey, byte[] info, int outLengthBytes) {
        return expand(HkdfMacFactory.Default.hmacSha256(), pseudoRandomKey, info, outLengthBytes);
    }

    /**
     * The second stage "expands" the pseudorandom key to the desired
     * length; the number and lengths of the output keys depend on the
     * specific cryptographic algorithms for which the keys are needed.
     * <p>
     * Uses Hmac with Sha512.
     *
     * @param pseudoRandomKey a pseudorandom key of at least hmac hash length in bytes (usually, the output from the extract step)
     * @param info            optional context and application specific information; may be null
     * @param outLengthBytes  length of output keying material in bytes
     * @return new byte array of output keying material (OKM)
     */
    public static byte[] expandHmacSha512(byte[] pseudoRandomKey, byte[] info, int outLengthBytes) {
        return expand(HkdfMacFactory.Default.hmacSha512(), pseudoRandomKey, info, outLengthBytes);
    }

    /**
     * The second stage "expands" the pseudorandom key to the desired
     * length; the number and lengths of the output keys depend on the
     * specific cryptographic algorithms for which the keys are needed.
     *
     * @param macFactory      factory creating the used mac algorithm
     * @param pseudoRandomKey a pseudorandom key of at least hmac hash length in bytes (usually, the output from the extract step)
     * @param info            optional context and application specific information; may be null
     * @param outLengthBytes  length of output keying material in bytes
     * @return new byte array of output keying material (OKM)
     */
    public static byte[] expand(HkdfMacFactory macFactory, byte[] pseudoRandomKey, byte[] info, int outLengthBytes) {
        return new Expander(macFactory).hkdfExpand(pseudoRandomKey, info, outLengthBytes);
    }    
    
    /* ********************************************************************* EXTRACT & EXPAND */

    /**
     * Convenience method for extract &amp; expand in a single method.
     * <p>
     * Uses Hmac with Sha256.
     *
     * @param inputKeyingMaterial data to be extracted (IKM)
     * @param saltExtract         optional salt value (a non-secret random value);
     * @param infoExpand          optional context and application specific information; may be null
     * @param outLengthByte       length of output keying material in bytes
     * @return new byte array of output keying material (OKM)
     */
    public static byte[] hkdfSha256(byte[] inputKeyingMaterial, byte[] saltExtract, byte[] infoExpand, int outLengthByte) {
        return hkdf(HkdfMacFactory.Default.hmacSha256(), inputKeyingMaterial, saltExtract, infoExpand, outLengthByte);
    }

    /**
     * Convenience method for extract &amp; expand in a single method
     *
     * @param macFactory          factory creating the used mac algorithm
     * @param inputKeyingMaterial data to be extracted (IKM)
     * @param saltExtract         optional salt value (a non-secret random value);
     * @param infoExpand          optional context and application specific information; may be null
     * @param outLengthByte       length of output keying material in bytes
     * @return new byte array of output keying material (OKM)
     */
    public static byte[] hkdf(HkdfMacFactory macFactory, byte[] inputKeyingMaterial, byte[] saltExtract, byte[] infoExpand, int outLengthByte) {
        return new Expander(macFactory).hkdfExpand(new Extractor(macFactory).hkdfExtract(inputKeyingMaterial, saltExtract), infoExpand, outLengthByte);
    }
    
    /* ************************************************************************** IMPL */

    static final class Extractor {
        private final HkdfMacFactory macFactory;

        Extractor(HkdfMacFactory macFactory) {
            this.macFactory = macFactory;
        }

        /**
         * Step 1 of RFC 5869
         * <p>
         * The first stage takes the input keying material and "extracts" from it a fixed-length pseudorandom
         * key K. The goal of the "extract" stage is to "concentrate" the possibly dispersed entropy of the input
         * keying material into a short, but cryptographically strong, pseudorandom key.
         *
         * @param inputKeyingMaterial data to be extracted (IKM)
         * @param salt                optional salt value (a non-secret random value);
         *                            if not provided, it is set to a array of hash length of zeros.
         * @return a new byte array pseudorandom key (of hash length in bytes) (PRK) which can be used to expand
         */
        byte[] hkdfExtract(byte[] inputKeyingMaterial, byte[] salt) {
            if (salt == null || salt.length == 0) {
                salt = new byte[macFactory.macHashLengthByte()];
            }

            if (inputKeyingMaterial == null || inputKeyingMaterial.length <= 0) {
                throw new IllegalArgumentException("provided inputKeyingMaterial must be at least of size 1 and not null");
            }

            Mac mac = macFactory.createMacInstance(salt);
            mac.update(inputKeyingMaterial);
            return mac.doFinal();
        }
    }

    static final class Expander {
        private final HkdfMacFactory macFactory;

        Expander(HkdfMacFactory macFactory) {
            this.macFactory = macFactory;
        }

        /**
         * Step 2 of RFC 5869.
         * <p>
         * The second stage "expands" the pseudorandom key to the desired
         * length; the number and lengths of the output keys depend on the
         * specific cryptographic algorithms for which the keys are needed.
         *
         * @param pseudoRandomKey a pseudorandom key of at least hmac hash length in bytes (usually, the output from the extract step)
         * @param info            optional context and application specific information; may be null
         * @param outLengthBytes  length of output keying material in bytes (must be <= 255 * mac hash length)
         * @return new byte array of output keying material (OKM)
         */
        byte[] hkdfExpand(byte[] pseudoRandomKey, byte[] info, int outLengthBytes) {

            if (outLengthBytes <= 0) {
                throw new IllegalArgumentException("out length bytes must be at least 1");
            }

            if (pseudoRandomKey == null || pseudoRandomKey.length <= 0) {
                throw new IllegalArgumentException("provided pseudoRandomKey must be at least of size 1 and not null");
            }

            Mac hmacHasher = macFactory.createMacInstance(pseudoRandomKey);

            if (info == null) {
                info = new byte[0];
            }

            byte[] blockN = new byte[0];

            int iterations = (int) Math.ceil(((double) outLengthBytes) / ((double) macFactory.macHashLengthByte()));

            if (iterations > 255) {
                throw new IllegalArgumentException("out length must be maximal 255 * hash len");
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream(outLengthBytes);
            int remainingBytes = outLengthBytes;

            for (int i = 0; i < iterations; i++) {
                hmacHasher.update(blockN);
                hmacHasher.update(info);
                hmacHasher.update((byte) (i + 1));

                blockN = hmacHasher.doFinal();

                int stepSize = Math.min(remainingBytes, blockN.length);

                stream.write(blockN, 0, stepSize);
                remainingBytes -= stepSize;
            }

            return stream.toByteArray();
        }
    }
}