/* Copyright (C) 2016 Advanced Digital Science Centre

        * This file is part of Soft-Grid.
        * For more information visit https://www.illinois.adsc.com.sg/cybersage/
        *
        * Soft-Grid is free software: you can redistribute it and/or modify
        * it under the terms of the GNU General Public License as published by
        * the Free Software Foundation, either version 3 of the License, or
        * (at your option) any later version.
        *
        * Soft-Grid is distributed in the hope that it will be useful,
        * but WITHOUT ANY WARRANTY; without even the implied warranty of
        * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        * GNU General Public License for more details.
        *
        * You should have received a copy of the GNU General Public License
        * along with Soft-Grid.  If not, see <http://www.gnu.org/licenses/>.

        * @author Prageeth Mahendra Gunathilaka
*/
/**
 * This class file was automatically generated by jASN1 (http://www.openmuc.org)
 */

package org.openmuc.openiec61850.internal.mms.asn1;

import java.io.IOException;
import java.io.InputStream;

import org.openmuc.jasn1.ber.BerByteArrayOutputStream;
import org.openmuc.jasn1.ber.BerIdentifier;
import org.openmuc.jasn1.ber.BerLength;
import org.openmuc.jasn1.ber.types.BerInteger;
import org.openmuc.jasn1.ber.types.string.BerVisibleString;

public final class ServiceError {

	public final static class SubChoice_errorClass {

		public byte[] code = null;
		public BerInteger vmd_state = null;

		public BerInteger application_reference = null;

		public BerInteger definition = null;

		public BerInteger resource = null;

		public BerInteger service = null;

		public BerInteger service_preempt = null;

		public BerInteger time_resolution = null;

		public BerInteger access = null;

		public BerInteger initiate = null;

		public BerInteger conclude = null;

		public BerInteger cancel = null;

		public BerInteger file = null;

		public BerInteger others = null;

		public SubChoice_errorClass() {
		}

		public SubChoice_errorClass(byte[] code) {
			this.code = code;
		}

		public SubChoice_errorClass(BerInteger vmd_state, BerInteger application_reference, BerInteger definition,
				BerInteger resource, BerInteger service, BerInteger service_preempt, BerInteger time_resolution,
				BerInteger access, BerInteger initiate, BerInteger conclude, BerInteger cancel, BerInteger file,
				BerInteger others) {
			this.vmd_state = vmd_state;
			this.application_reference = application_reference;
			this.definition = definition;
			this.resource = resource;
			this.service = service;
			this.service_preempt = service_preempt;
			this.time_resolution = time_resolution;
			this.access = access;
			this.initiate = initiate;
			this.conclude = conclude;
			this.cancel = cancel;
			this.file = file;
			this.others = others;
		}

		public int encode(BerByteArrayOutputStream berOStream, boolean explicit) throws IOException {
			if (code != null) {
				for (int i = code.length - 1; i >= 0; i--) {
					berOStream.write(code[i]);
				}
				return code.length;

			}
			int codeLength = 0;
			if (others != null) {
				codeLength += others.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 12))
						.encode(berOStream);
				return codeLength;

			}

			if (file != null) {
				codeLength += file.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 11))
						.encode(berOStream);
				return codeLength;

			}

			if (cancel != null) {
				codeLength += cancel.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 10))
						.encode(berOStream);
				return codeLength;

			}

			if (conclude != null) {
				codeLength += conclude.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 9))
						.encode(berOStream);
				return codeLength;

			}

			if (initiate != null) {
				codeLength += initiate.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 8))
						.encode(berOStream);
				return codeLength;

			}

			if (access != null) {
				codeLength += access.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 7))
						.encode(berOStream);
				return codeLength;

			}

			if (time_resolution != null) {
				codeLength += time_resolution.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 6))
						.encode(berOStream);
				return codeLength;

			}

			if (service_preempt != null) {
				codeLength += service_preempt.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 5))
						.encode(berOStream);
				return codeLength;

			}

			if (service != null) {
				codeLength += service.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 4))
						.encode(berOStream);
				return codeLength;

			}

			if (resource != null) {
				codeLength += resource.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 3))
						.encode(berOStream);
				return codeLength;

			}

			if (definition != null) {
				codeLength += definition.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 2))
						.encode(berOStream);
				return codeLength;

			}

			if (application_reference != null) {
				codeLength += application_reference.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 1))
						.encode(berOStream);
				return codeLength;

			}

			if (vmd_state != null) {
				codeLength += vmd_state.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 0))
						.encode(berOStream);
				return codeLength;

			}

			throw new IOException("Error encoding BerChoice: No item in choice was selected.");
		}

		public int decode(InputStream iStream, BerIdentifier berIdentifier) throws IOException {
			int codeLength = 0;
			BerIdentifier passedIdentifier = berIdentifier;
			if (berIdentifier == null) {
				berIdentifier = new BerIdentifier();
				codeLength += berIdentifier.decode(iStream);
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 0)) {
				vmd_state = new BerInteger();
				codeLength += vmd_state.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 1)) {
				application_reference = new BerInteger();
				codeLength += application_reference.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 2)) {
				definition = new BerInteger();
				codeLength += definition.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 3)) {
				resource = new BerInteger();
				codeLength += resource.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 4)) {
				service = new BerInteger();
				codeLength += service.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 5)) {
				service_preempt = new BerInteger();
				codeLength += service_preempt.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 6)) {
				time_resolution = new BerInteger();
				codeLength += time_resolution.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 7)) {
				access = new BerInteger();
				codeLength += access.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 8)) {
				initiate = new BerInteger();
				codeLength += initiate.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 9)) {
				conclude = new BerInteger();
				codeLength += conclude.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 10)) {
				cancel = new BerInteger();
				codeLength += cancel.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 11)) {
				file = new BerInteger();
				codeLength += file.decode(iStream, false);
				return codeLength;
			}

			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 12)) {
				others = new BerInteger();
				codeLength += others.decode(iStream, false);
				return codeLength;
			}

			if (passedIdentifier != null) {
				return 0;
			}
			throw new IOException("Error decoding BerChoice: Identifier matched to no item.");
		}

		public void encodeAndSave(int encodingSizeGuess) throws IOException {
			BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(encodingSizeGuess);
			encode(berOStream, false);
			code = berOStream.getArray();
		}
	}

	public final static BerIdentifier identifier = new BerIdentifier(BerIdentifier.UNIVERSAL_CLASS,
			BerIdentifier.CONSTRUCTED, 16);
	protected BerIdentifier id;

	public byte[] code = null;
	public SubChoice_errorClass errorClass = null;

	public BerInteger additionalCode = null;

	public BerVisibleString additionalDescription = null;

	public ServiceError() {
		id = identifier;
	}

	public ServiceError(byte[] code) {
		id = identifier;
		this.code = code;
	}

	public ServiceError(SubChoice_errorClass errorClass, BerInteger additionalCode,
			BerVisibleString additionalDescription) {
		id = identifier;
		this.errorClass = errorClass;
		this.additionalCode = additionalCode;
		this.additionalDescription = additionalDescription;
	}

	public int encode(BerByteArrayOutputStream berOStream, boolean explicit) throws IOException {

		int codeLength;

		if (code != null) {
			codeLength = code.length;
			for (int i = code.length - 1; i >= 0; i--) {
				berOStream.write(code[i]);
			}
		}
		else {
			codeLength = 0;
			int sublength;

			if (additionalDescription != null) {
				codeLength += additionalDescription.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 2))
						.encode(berOStream);
			}

			if (additionalCode != null) {
				codeLength += additionalCode.encode(berOStream, false);
				codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 1))
						.encode(berOStream);
			}

			sublength = errorClass.encode(berOStream, true);
			codeLength += sublength;
			codeLength += BerLength.encodeLength(berOStream, sublength);
			codeLength += (new BerIdentifier(BerIdentifier.CONTEXT_CLASS, BerIdentifier.CONSTRUCTED, 0))
					.encode(berOStream);

			codeLength += BerLength.encodeLength(berOStream, codeLength);
		}

		if (explicit) {
			codeLength += id.encode(berOStream);
		}

		return codeLength;

	}

	public int decode(InputStream iStream, boolean explicit) throws IOException {
		int codeLength = 0;
		int subCodeLength = 0;
		int choiceDecodeLength = 0;
		BerIdentifier berIdentifier = new BerIdentifier();
		boolean decodedIdentifier = false;

		if (explicit) {
			codeLength += id.decodeAndCheck(iStream);
		}

		BerLength length = new BerLength();
		codeLength += length.decode(iStream);

		if (subCodeLength < length.val) {
			if (decodedIdentifier == false) {
				subCodeLength += berIdentifier.decode(iStream);
				decodedIdentifier = true;
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.CONSTRUCTED, 0)) {
				subCodeLength += new BerLength().decode(iStream);
				errorClass = new SubChoice_errorClass();
				choiceDecodeLength = errorClass.decode(iStream, null);
				if (choiceDecodeLength != 0) {
					decodedIdentifier = false;
					subCodeLength += choiceDecodeLength;
				}
			}
			else {
				throw new IOException("Identifier does not macht required sequence element identifer.");
			}
		}
		if (subCodeLength < length.val) {
			if (decodedIdentifier == false) {
				subCodeLength += berIdentifier.decode(iStream);
				decodedIdentifier = true;
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 1)) {
				additionalCode = new BerInteger();
				subCodeLength += additionalCode.decode(iStream, false);
				decodedIdentifier = false;
			}
		}
		if (subCodeLength < length.val) {
			if (decodedIdentifier == false) {
				subCodeLength += berIdentifier.decode(iStream);
				decodedIdentifier = true;
			}
			if (berIdentifier.equals(BerIdentifier.CONTEXT_CLASS, BerIdentifier.PRIMITIVE, 2)) {
				additionalDescription = new BerVisibleString();
				subCodeLength += additionalDescription.decode(iStream, false);
				decodedIdentifier = false;
			}
		}
		if (subCodeLength != length.val) {
			throw new IOException("Decoded sequence has wrong length tag");

		}
		codeLength += subCodeLength;

		return codeLength;
	}

	public void encodeAndSave(int encodingSizeGuess) throws IOException {
		BerByteArrayOutputStream berOStream = new BerByteArrayOutputStream(encodingSizeGuess);
		encode(berOStream, false);
		code = berOStream.getArray();
	}
}
