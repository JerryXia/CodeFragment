﻿namespace Cassini
{
    using System;
    using System.Collections;
    using System.Reflection;
    using System.Text;

    internal class ByteString
    {
        private byte[] _bytes;
        private int _length;
        private int _offset;

        public ByteString(byte[] bytes, int offset, int length)
        {
            this._bytes = bytes;
            this._offset = offset;
            this._length = length;
        }

        public byte[] GetBytes()
        {
            byte[] dst = new byte[this._length];
            if (this._length > 0)
            {
                Buffer.BlockCopy(this._bytes, this._offset, dst, 0, this._length);
            }
            return dst;
        }

        public string GetString()
        {
            return this.GetString(Encoding.UTF8);
        }

        public string GetString(Encoding enc)
        {
            if (this.IsEmpty)
            {
                return string.Empty;
            }
            return enc.GetString(this._bytes, this._offset, this._length);
        }

        public int IndexOf(char ch)
        {
            return this.IndexOf(ch, 0);
        }

        public int IndexOf(char ch, int offset)
        {
            for (int i = offset; i < this._length; i++)
            {
                if (this[i] == ((byte) ch))
                {
                    return i;
                }
            }
            return -1;
        }

        public ByteString[] Split(char sep)
        {
            int index;
            ArrayList list = new ArrayList();
            int offset = 0;
            while (offset < this._length)
            {
                index = this.IndexOf(sep, offset);
                if (index < 0)
                {
                    break;
                }
                list.Add(this.Substring(offset, index - offset));
                offset = index + 1;
                while ((this[offset] == ((byte) sep)) && (offset < this._length))
                {
                    offset++;
                }
            }
            if (offset < this._length)
            {
                list.Add(this.Substring(offset));
            }
            int count = list.Count;
            ByteString[] strArray = new ByteString[count];
            for (index = 0; index < count; index++)
            {
                strArray[index] = (ByteString) list[index];
            }
            return strArray;
        }

        public ByteString Substring(int offset)
        {
            return this.Substring(offset, this._length - offset);
        }

        public ByteString Substring(int offset, int len)
        {
            return new ByteString(this._bytes, this._offset + offset, len);
        }

        public byte[] Bytes
        {
            get
            {
                return this._bytes;
            }
        }

        public bool IsEmpty
        {
            get
            {
                return ((this._bytes == null) || (this._length == 0));
            }
        }

        public byte this[int index]
        {
            get
            {
                return this._bytes[this._offset + index];
            }
        }

        public int Length
        {
            get
            {
                return this._length;
            }
        }

        public int Offset
        {
            get
            {
                return this._offset;
            }
        }
    }
}

