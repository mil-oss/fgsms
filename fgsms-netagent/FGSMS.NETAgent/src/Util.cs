/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * If it is not possible or desirable to put the notice in a particular
 * file, then You may include the notice in a location (such as a LICENSE
 * file in a relevant directory) where a recipient would be likely to look
 * for such a notice.

 * 
 */
 
/*  ---------------------------------------------------------------------------
 *  US Government, Department of the Army
 *  Army Materiel Command
 *  Research Development Engineering Command
 *  Communications Electronics Research Development and Engineering Center
 *  ---------------------------------------------------------------------------
 */

using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Security.Cryptography;

using System.Text.RegularExpressions;
using System.Configuration;
using System.Diagnostics;
using System.Web;


namespace org.miloss.fgsms.agent
{
    public class Util
    {
        private SymmetricAlgorithm _crypto = new RijndaelManaged();
        private const string _key = "38aAv2L3oohxIRWsO3nVNmhI4Z/yP6bU6aSkMpZWatc=";
        private const string _iv = "pT+0602xdeGNZ/gSAsxb6A==";
        private const int _BufferSize = 2048;

        public Util()
        {
            _crypto.KeySize = 256;
            _crypto.Key = Convert.FromBase64String(_key);
            _crypto.IV = Convert.FromBase64String(_iv);

            //this.IntializationVector = new object(_DefaultIntializationVector);
            /* _crypto.GenerateKey();
             _crypto.GenerateIV();
             byte[] iv = _crypto.IV;
             byte[] key = _crypto.Key;

             string ivs = Convert.ToBase64String(iv);
             MessageBox.Show(ivs);
             string keys = Convert.ToBase64String(key);
             MessageBox.Show(keys);*/
        }
        /// <summary>
        /// encrypts a string using AES 256
        /// </summary>
        /// <param name="clear"></param>
        /// <returns></returns>
        public string EN(string clear)
        {
            byte[] data = Encoding.UTF8.GetBytes(clear);
            System.IO.MemoryStream ms = new MemoryStream();
            CryptoStream cs = new CryptoStream(ms, _crypto.CreateEncryptor(), CryptoStreamMode.Write);
            cs.Write(data, 0, data.Length);
            cs.Close();
            ms.Close();
            return Convert.ToBase64String(ms.ToArray());
            //return clear;
        }

        /// <summary>
        /// expects a base64 encoded string that is aes encrypted
        /// </summary>
        /// <param name="cipher"></param>
        /// <returns></returns>
        public string DE(string cipher)
        {
            if (String.IsNullOrEmpty(cipher))
                return cipher;
            try
            {
                byte[] data = Convert.FromBase64String(cipher);

                System.IO.MemoryStream msen = new MemoryStream();
                System.IO.MemoryStream msde = new MemoryStream();
                msen.Write(data, 0, data.Length);
                msen.Seek(0, SeekOrigin.Begin);
                data = null;

                CryptoStream cs = new CryptoStream(msen, _crypto.CreateDecryptor(), CryptoStreamMode.Read);

                byte[] buff = new byte[_BufferSize];
                int i = cs.Read(buff, 0, _BufferSize);
                do
                {
                    msde.Write(buff, 0, i);
                    i = cs.Read(buff, 0, _BufferSize);
                }
                while (i > 0);
                cs.Close();
                msen.Close();
                msde.Close();
                return Encoding.UTF8.GetString(msde.ToArray());
            }
            catch (Exception ex)
            {
                return "";
            }
        }





    }

   
}
